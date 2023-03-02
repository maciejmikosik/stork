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
    if (!stack.hasArgument()) {
      return computation;
    } else if (computation.expression instanceof EagerInstruction eager && !eager.visited) {
      return computation(
          stack.argument(),
          stack.pop().pushFunction(eager.visit()));
    } else if (computation.expression instanceof Instruction instruction) {
      return computation(
          instruction.apply(stack.argument()),
          stack.pop());
    } else {
      return computation;
    }
  }
}
