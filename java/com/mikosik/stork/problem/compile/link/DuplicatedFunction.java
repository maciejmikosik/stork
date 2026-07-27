package com.mikosik.stork.problem.compile.link;

import com.mikosik.stork.model.exp.Identifier;

public class DuplicatedFunction extends CannotLink {
  public final Identifier function;

  private DuplicatedFunction(Identifier function) {
    this.function = function;
  }

  public static DuplicatedFunction duplicatedFunction(Identifier function) {
    return new DuplicatedFunction(function);
  }
}
