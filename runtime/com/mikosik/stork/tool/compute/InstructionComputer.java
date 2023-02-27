package com.mikosik.stork.tool.compute;

import static com.mikosik.stork.model.Computation.computation;

import com.mikosik.stork.model.Computation;
import com.mikosik.stork.model.Eager;
import com.mikosik.stork.model.Instruction;
import com.mikosik.stork.model.Stack;

public class InstructionComputer implements Computer {
  private InstructionComputer() {}

  public static Computer instructionComputer() {
    return new InstructionComputer();
  }

  public Computation compute(Computation computation) {
    Stack stack = computation.stack;
    if (!stack.hasArgument()) {
      return computation;
    } else if (computation.expression instanceof Eager eager) {
      return computation(
          stack.argument(),
          stack.pop().pushFunction(eager.instruction));
    } else if (computation.expression instanceof Instruction instruction) {
      return computation(
          instruction.apply(stack.argument()),
          stack.pop());
    } else {
      return computation;
    }
  }
}
