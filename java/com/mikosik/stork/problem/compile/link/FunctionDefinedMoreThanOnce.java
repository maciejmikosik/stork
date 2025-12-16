package com.mikosik.stork.problem.compile.link;

import static com.mikosik.stork.problem.Description.description;

import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.problem.Description;

public class FunctionDefinedMoreThanOnce extends CannotLink {
  public final Identifier function;

  private FunctionDefinedMoreThanOnce(Identifier function) {
    this.function = function;
  }

  public static FunctionDefinedMoreThanOnce functionDefinedMoreThanOnce(Identifier function) {
    return new FunctionDefinedMoreThanOnce(function);
  }

  public Description describe() {
    return description("function [%s] is defined more than once"
        .formatted(function.name()));
  }
}
