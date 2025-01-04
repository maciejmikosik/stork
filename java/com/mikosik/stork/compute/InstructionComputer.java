package com.mikosik.stork.compute;

import static com.mikosik.stork.compute.Computation.computation;
import static com.mikosik.stork.compute.RequiringArgument.requiringArgument;

import com.mikosik.stork.compute.Stack.Argument;
import com.mikosik.stork.model.EagerInstruction;
import com.mikosik.stork.model.Instruction;

public class InstructionComputer implements Computer {
  private InstructionComputer() {}

  public static Computer instructionComputer() {
    return requiringArgument(new InstructionComputer());
  }

  public Computation compute(Computation computation) {
    var stack = computation.stack;
    return switch (computation.expression) {
      case EagerInstruction eager when !eager.visited -> switch (stack) {
        case Argument argument -> computation(
            argument.expression,
            argument.previous.pushFunction(eager.visit()));
        default -> computation;
      };
      case Instruction instruction -> switch (stack) {
        case Argument argument -> computation(
            instruction.apply(argument.expression),
            argument.previous);
        default -> computation;
      };
      default -> computation;
    };
  }
}
