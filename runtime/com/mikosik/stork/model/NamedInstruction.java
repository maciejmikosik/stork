package com.mikosik.stork.model;

import static com.mikosik.stork.model.Application.application;

public class NamedInstruction implements Instruction {
  public final Expression name;
  public final Instruction instruction;

  private NamedInstruction(Expression name, Instruction instruction) {
    this.name = name;
    this.instruction = instruction;
  }

  public static NamedInstruction name(Expression name, Instruction instruction) {
    return new NamedInstruction(name, instruction);
  }

  public Expression apply(Expression argument) {
    Expression applied = instruction.apply(argument);
    return applied instanceof Instruction appliedInstruction
        ? name(application(name, argument), appliedInstruction)
        : applied;
  }
}
