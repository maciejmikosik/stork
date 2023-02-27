package com.mikosik.stork.model;

public class Eager implements Expression {
  public final Instruction instruction;

  private Eager(Instruction instruction) {
    this.instruction = instruction;
  }

  public static Expression eager(Instruction instruction) {
    return new Eager(instruction);
  }
}
