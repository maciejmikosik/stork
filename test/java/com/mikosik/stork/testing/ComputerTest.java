package com.mikosik.stork.testing;

import static com.mikosik.stork.common.Chain.add;
import static com.mikosik.stork.common.Chain.empty;
import static com.mikosik.stork.common.Chains.chainOf;
import static com.mikosik.stork.common.Chains.map;
import static com.mikosik.stork.data.model.Module.module;
import static com.mikosik.stork.data.model.comp.Computation.computation;
import static com.mikosik.stork.tool.Default.compileExpression;
import static com.mikosik.stork.tool.common.Computations.abort;
import static com.mikosik.stork.tool.common.Expressions.print;
import static com.mikosik.stork.tool.comp.WirableComputer.computer;
import static com.mikosik.stork.tool.link.DefaultLinker.defaultLinker;
import static com.mikosik.stork.tool.link.NoncollidingLinker.noncolliding;
import static java.lang.String.format;
import static java.util.Objects.deepEquals;

import java.util.List;
import java.util.function.BiFunction;

import org.quackery.Body;
import org.quackery.Test;
import org.quackery.report.AssertException;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.tool.Default;
import com.mikosik.stork.tool.comp.Computer;
import com.mikosik.stork.tool.link.Linker;

public class ComputerTest implements Test {
  private final String name;
  private Chain<String> givenCode = empty();
  private final String whenCode;
  private final String thenCode;

  private ComputerTest(
      String name,
      Chain<String> givenCode,
      String whenCode,
      String thenCode) {
    this.name = name;
    this.givenCode = givenCode;
    this.whenCode = whenCode;
    this.thenCode = thenCode;
  }

  public static ComputerTest computerTest(String name) {
    return new ComputerTest(name, empty(), null, null);
  }

  public ComputerTest given(String code) {
    return new ComputerTest(
        name,
        add(code, givenCode),
        whenCode,
        thenCode);
  }

  public ComputerTest when(String code) {
    return new ComputerTest(
        name,
        givenCode,
        code,
        thenCode);
  }

  public ComputerTest then(String code) {
    return new ComputerTest(
        name,
        givenCode,
        whenCode,
        code);
  }

  public <R> R visit(
      BiFunction<String, Body, R> caseHandler,
      BiFunction<String, List<Test>, R> suiteHandler) {
    return caseHandler.apply(name, () -> run());
  }

  private void run() {
    Chain<Definition> definitions = map(Default::compileDefinition, givenCode);
    Chain<Module> modules = chainOf(module(definitions));
    Linker linker = noncolliding(defaultLinker());
    Module linkedModule = linker.link(modules);

    Computer computer = computer()
        .module(linkedModule)
        .opcoding()
        .substituting()
        .stacking()
        .interruptible()
        .wire(MockingComputer::mocking)
        .exhausted()
        .looping();

    String computedWhenCode = compute(whenCode, computer);
    String computedThenCode = compute(thenCode, computer);

    if (!deepEquals(computedWhenCode, computedThenCode)) {
      throw new AssertException(format(""
          + "expected that expression\n"
          + "  %s\n"
          + "is equal to\n"
          + "  %s\n"
          + "which computes to\n"
          + "  %s\n"
          + "but expression computed to\n"
          + "  %s\n",
          whenCode,
          thenCode,
          computedThenCode,
          computedWhenCode));
    }
  }

  private static String compute(String code, Computer computer) {
    return print(abort(computer.compute(computation(compileExpression(code)))));
  }
}
