package com.mikosik.stork.testing;

import static com.mikosik.stork.common.Chain.add;
import static com.mikosik.stork.common.Chain.empty;
import static com.mikosik.stork.common.Chains.map;
import static com.mikosik.stork.common.Functions.none;
import static com.mikosik.stork.data.model.Module.module;
import static com.mikosik.stork.data.model.comp.Computation.computation;
import static com.mikosik.stork.testing.MockingComputer.mocking;
import static com.mikosik.stork.tool.Default.compileExpression;
import static com.mikosik.stork.tool.common.Computations.abort;
import static com.mikosik.stork.tool.common.Expressions.print;
import static com.mikosik.stork.tool.comp.WirableComputer.computer;
import static com.mikosik.stork.tool.link.DefaultLinker.defaultLinker;
import static com.mikosik.stork.tool.link.NoncollidingLinker.noncolliding;
import static java.lang.String.format;
import static java.util.Arrays.asList;

import java.util.HashSet;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import org.quackery.Body;
import org.quackery.Test;
import org.quackery.report.AssertException;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.lib.Modules;
import com.mikosik.stork.tool.Default;
import com.mikosik.stork.tool.comp.Computer;
import com.mikosik.stork.tool.comp.HumaneComputer;
import com.mikosik.stork.tool.link.Linker;

public class StorkTest implements Test {
  private String name;
  private boolean humane;
  private Chain<String> givenImportedModules = empty();
  private Predicate<String> mockPredicate = none();
  private Chain<String> givenDefinitions = empty();
  private String whenExpression;
  private String thenReturnedExpression;

  private StorkTest() {}

  public static StorkTest storkTest() {
    return new StorkTest();
  }

  public static StorkTest storkTest(String name) {
    return storkTest().name(name);
  }

  public StorkTest name(String name) {
    StorkTest copy = copy();
    copy.name = name;
    return copy;
  }

  public StorkTest humane() {
    StorkTest copy = copy();
    copy.humane = true;
    return copy;
  }

  public StorkTest givenImported(String module) {
    StorkTest copy = copy();
    copy.givenImportedModules = add(module, givenImportedModules);
    return copy;
  }

  public StorkTest givenMocks(Predicate<String> mockPredicate) {
    StorkTest copy = copy();
    copy.mockPredicate = mockPredicate;
    return copy;
  }

  public StorkTest givenMocks(String... mocks) {
    StorkTest copy = copy();
    copy.mockPredicate = new HashSet<>(asList(mocks))::contains;
    return copy;
  }

  public StorkTest given(String definition) {
    StorkTest copy = copy();
    copy.givenDefinitions = add(definition, givenDefinitions);
    return copy;
  }

  public StorkTest when(String expression) {
    StorkTest copy = copy();
    copy.whenExpression = expression;
    return copy;
  }

  public StorkTest thenReturned(String expression) {
    StorkTest copy = copy();
    copy.thenReturnedExpression = expression;
    return copy;
  }

  private StorkTest copy() {
    StorkTest copy = new StorkTest();
    copy.name = name;
    copy.humane = humane;
    copy.givenImportedModules = givenImportedModules;
    copy.mockPredicate = mockPredicate;
    copy.givenDefinitions = givenDefinitions;
    copy.whenExpression = whenExpression;
    copy.thenReturnedExpression = thenReturnedExpression;
    return copy;
  }

  public <R> R visit(
      BiFunction<String, Body, R> caseHandler,
      BiFunction<String, List<Test>, R> suiteHandler) {
    return caseHandler.apply(name, () -> run());
  }

  private void run() {
    Chain<Definition> definitions = map(Default::compileDefinition, givenDefinitions);
    Chain<Module> modules = map(Modules::module, givenImportedModules);
    Chain<Module> allModules = add(module(definitions), modules);

    Linker linker = noncolliding(defaultLinker());
    Module linkedModule = linker.link(allModules);
    Computer computer = computer()
        .module(linkedModule)
        .opcoding()
        .substituting()
        .stacking()
        .interruptible()
        .wire(c -> mocking(mockPredicate, c))
        .wire(this::maybeHumane)
        .exhausted()
        .looping();
    Expression actual = abort(computer.compute(computation(
        compileExpression(whenExpression))));
    Expression expected = abort(computer.compute(computation(
        compileExpression(thenReturnedExpression))));

    if (!expected.toString().equals(actual.toString())) {
      throw new AssertException(format(""
          + "expected that expression\n"
          + "  %s\n"
          + "returns\n"
          + "  %s\n"
          + "which evaluates to\n"
          + "  %s\n"
          + "but returned\n"
          + "  %s\n",
          whenExpression,
          thenReturnedExpression,
          print(expected),
          print(actual)));
    }
  }

  private Computer maybeHumane(Computer computer) {
    return humane
        ? HumaneComputer.humane(computer)
        : computer;
  }
}
