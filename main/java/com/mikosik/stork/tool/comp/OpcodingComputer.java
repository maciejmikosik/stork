package com.mikosik.stork.tool.comp;

import static com.mikosik.stork.data.model.comp.Computation.computation;
import static com.mikosik.stork.data.model.comp.Function.function;
import static com.mikosik.stork.tool.common.Computations.isComputable;
import static com.mikosik.stork.tool.common.Translate.asStorkBoolean;
import static com.mikosik.stork.tool.common.Translate.asStorkInteger;
import static com.mikosik.stork.tool.comp.Opcode.opcode;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Variable;
import com.mikosik.stork.data.model.comp.Argument;
import com.mikosik.stork.data.model.comp.Computation;

public class OpcodingComputer implements Computer {
  private final Map<String, Expression> opcodes = opcodes();
  private final Computer nextComputer;

  private OpcodingComputer(Computer nextComputer) {
    this.nextComputer = nextComputer;
  }

  public static Computer opcoding(Computer nextComputer) {
    return new OpcodingComputer(nextComputer);
  }

  public Computation compute(Computation computation) {
    if (computation.expression instanceof Variable) {
      Variable variable = (Variable) computation.expression;
      return Optional
          .ofNullable(opcodes.get(variable.name))
          .map(opcode -> computation(opcode, computation.stack))
          .orElseGet(() -> nextComputer.compute(computation));
    } else if (computation.expression instanceof Opcode) {
      Opcode opcode = (Opcode) computation.expression;
      Argument argument = (Argument) computation.stack;
      return isComputable(argument.expression)
          ? computation(
              argument.expression,
              function(opcode, argument.stack))
          : computation(
              opcode.apply(argument.expression).invokeIfReady(),
              argument.stack);
    } else {
      return nextComputer.compute(computation);
    }
  }

  // TODO make opcoding computer parametrized by opcodes
  private Map<String, Expression> opcodes() {
    Map<String, Expression> opcodes = new HashMap<String, Expression>();
    opcodes.put("negate", opcode()
        .name("opcode.negate")
        .nArguments(1)
        .operatorInt(number -> asStorkInteger(number.negate())));
    opcodes.put("add", opcode()
        .name("opcode.add")
        .nArguments(2)
        .operatorIntInt((a, b) -> asStorkInteger(a.add(b))));
    opcodes.put("equal", opcode()
        .name("opcode.equal")
        .nArguments(2)
        .operatorIntInt((a, b) -> asStorkBoolean(a.equals(b))));
    opcodes.put("moreThan", opcode()
        .name("opcode.moreThan")
        .nArguments(2)
        .operatorIntInt((a, b) -> asStorkBoolean(b.compareTo(a) > 0)));
    return opcodes;
  }
}
