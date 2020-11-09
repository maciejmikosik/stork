package com.mikosik.stork.testing;

import static com.mikosik.stork.data.model.Variable.variable;
import static com.mikosik.stork.testing.Assertions.areEqual;
import static com.mikosik.stork.testing.Assertions.failEqual;
import static com.mikosik.stork.tool.compile.Modeler.modelModule;
import static com.mikosik.stork.tool.compile.Parser.parse;
import static com.mikosik.stork.tool.compute.WirableComputer.computer;
import static org.quackery.Case.newCase;

import org.quackery.Test;

import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.tool.compute.CompleteComputer;

public class ComputerTest {
  public static Test computerTest(String name, String code) {
    return newCase(name, () -> run(code));
  }

  private static void run(String code) {
    Module module = modelModule(parse(code));
    CompleteComputer computer = computer()
        .moduling(module)
        .substituting()
        .stacking()
        .interruptible()
        .looping()
        .complete();

    Expression whenComputed = computer.compute(variable("when"));
    Expression thenComputed = computer.compute(variable("then"));

    if (!areEqual(whenComputed, thenComputed)) {
      throw failEqual(
          find("when", module),
          find("then", module),
          whenComputed,
          thenComputed);
    }
  }

  private static Expression find(String variableName, Module module) {
    return module.definitions.stream()
        .filter(definition -> definition.variable.name.contentEquals(variableName))
        .map(definition -> definition.expression)
        .findFirst()
        .orElseThrow(() -> new RuntimeException(variableName));
  }
}
