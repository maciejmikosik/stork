package com.mikosik.stork.tool.compute;

import static com.mikosik.stork.model.Computation.computation;

import com.mikosik.stork.model.Computation;
import com.mikosik.stork.model.Instruction;
import com.mikosik.stork.model.Stack;

public class InstructionComputer implements Computer {
  private InstructionComputer() {}

  public static Computer instructionComputer() {
    return new InstructionComputer();
  }

  public Computation compute(Computation computation) {
    return computation.expression instanceof Instruction instruction
        && computation.stack.hasArgument()
            ? compute(instruction, computation.stack)
            : computation;
  }

  private Computation compute(Instruction instruction, Stack stack) {
    return computation(
        instruction.apply(stack.argument()),
        stack.pop());
  }
}
