package com.mikosik.stork.compute;

import static com.mikosik.stork.compute.Computation.computation;

import com.mikosik.stork.model.EagerInstruction;
import com.mikosik.stork.model.Instruction;

public class InstructionComputer implements Computer {
  private InstructionComputer() {}

  public static Computer instructionComputer() {
    return new InstructionComputer();
  }

  public Computation compute(Computation computation) {
    Stack stack = computation.stack;
    // TODO create requireArgument Computer
    return !stack.hasArgument()
        ? computation
        : switch (computation.expression) {
          case EagerInstruction eager when !eager.visited -> computation(
              stack.argument(),
              stack.pop().pushFunction(eager.visit()));
          case Instruction instruction -> computation(
              instruction.apply(stack.argument()),
              stack.pop());
          default -> computation;
        };
  }
}
