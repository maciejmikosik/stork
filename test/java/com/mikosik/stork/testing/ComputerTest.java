package com.mikosik.stork.testing;

import static com.mikosik.stork.common.Chain.add;
import static com.mikosik.stork.common.Chain.empty;
import static com.mikosik.stork.common.Chains.map;
import static com.mikosik.stork.data.model.Module.module;
import static com.mikosik.stork.testing.Asserter.asserter;
import static com.mikosik.stork.tool.comp.WirableComputer.computer;

import java.util.List;
import java.util.function.BiFunction;

import org.quackery.Body;
import org.quackery.Test;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.tool.Default;
import com.mikosik.stork.tool.comp.Computer;

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

    Computer computer = computer()
        .moduling(module(definitions))
        .substituting()
        .stacking()
        .interruptible()
        .wire(MockingComputer::mocking)
        .looping();

    asserter(computer)
        .assertEqual(whenCode, thenCode);
  }
}
