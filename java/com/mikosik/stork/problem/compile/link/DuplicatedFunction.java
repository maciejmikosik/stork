package com.mikosik.stork.problem.compile.link;

import com.mikosik.stork.model.Variable;

public class DuplicatedFunction extends CannotLink {
  public final Variable function;

  private DuplicatedFunction(Variable function) {
    this.function = function;
  }

  public static DuplicatedFunction duplicatedFunction(
      Variable function) {
    return new DuplicatedFunction(function);
  }
}
