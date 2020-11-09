package com.mikosik.stork.testing;

import static com.mikosik.stork.data.model.Variable.variable;
import static com.mikosik.stork.tool.compile.Decompiler.decompiler;
import static com.mikosik.stork.tool.compile.Modeler.modelModule;
import static com.mikosik.stork.tool.compile.Parser.parse;
import static com.mikosik.stork.tool.compute.WirableComputer.computer;
import static java.lang.String.format;
import static java.util.Objects.deepEquals;
import static org.quackery.Case.newCase;

import org.quackery.Test;
import org.quackery.report.AssertException;

import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.tool.compile.Decompiler;
import com.mikosik.stork.tool.compute.CompleteComputer;

public class ComputerTest {
  public static Test computerTest(String name, String code) {
    return newCase(name, () -> {
      Module module = modelModule(parse(code));
      CompleteComputer computer = computer()
          .moduling(module)
          .substituting()
          .stacking()
          .interruptible()
          .looping()
          .complete();
      Decompiler decompiler = decompiler();

      Expression computedWhen = computer.compute(variable("when"));
      Expression computedThen = computer.compute(variable("then"));

      if (!areEqual(computedWhen, computedThen)) {
        Expression when = find("when", module);
        Expression then = find("then", module);
        throw new AssertException(format(""
            + "expected that expression\n"
            + "  %s\n"
            + "is equal to\n"
            + "  %s\n"
            + "which computes to\n"
            + "  %s\n"
            + "but expression computed to\n"
            + "  %s\n",
            decompiler.decompile(when),
            decompiler.decompile(then),
            decompiler.decompile(computedThen),
            decompiler.decompile(computedWhen)));
      }
    });
  }

  private static Expression find(String variableName, Module module) {
    return module.definitions.stream()
        .filter(definition -> definition.variable.name.contentEquals(variableName))
        .map(definition -> definition.expression)
        .findFirst()
        .orElseThrow(() -> new RuntimeException(variableName));
  }

  private static boolean areEqual(Expression first, Expression second) {
    Decompiler decompiler = decompiler();
    return deepEquals(
        decompiler.decompile(first),
        decompiler.decompile(second));
  }
}
