package com.mikosik.stork.problem.compile.link;

import com.mikosik.stork.model.Identifier;

public class FunctionDefinedMoreThanOnce implements CannotLink {
  public final Identifier function;

  private FunctionDefinedMoreThanOnce(Identifier function) {
    this.function = function;
  }

  public static FunctionDefinedMoreThanOnce functionDefinedMoreThanOnce(Identifier function) {
    return new FunctionDefinedMoreThanOnce(function);
  }

  public String description() {
    return "function [%s] is defined more than once"
        .formatted(function.name());
  }
}
