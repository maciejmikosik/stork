package com.mikosik.stork.model;

import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Identifier.identifier;

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

  public static NamedInstruction name(String name, Instruction instruction) {
    return name(identifier(name), instruction);
  }

  public Expression apply(Expression argument) {
    Expression applied = instruction.apply(argument);
    return switch (applied) {
      case NamedInstruction instruction -> instruction;
      case Instruction instruction -> name(application(name, argument), instruction);
      default -> applied;
    };
  }
}
