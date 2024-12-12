package com.mikosik.stork.model;

public class EagerInstruction implements Instruction {
  public final Instruction instruction;
  public final boolean visited;

  private EagerInstruction(Instruction instruction, boolean visited) {
    this.instruction = instruction;
    this.visited = visited;
  }

  public static EagerInstruction eager(Instruction instruction) {
    return new EagerInstruction(instruction, false);
  }

  public Expression visit() {
    return new EagerInstruction(instruction, true);
  }

  public Expression apply(Expression argument) {
    return eagerIfInstruction(instruction.apply(argument));
  }

  private static Expression eagerIfInstruction(Expression expression) {
    return switch (expression) {
      case Instruction instruction -> eager(instruction);
      default -> expression;
    };
  }
}
