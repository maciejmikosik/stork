package com.mikosik.stork.program;

import static java.lang.String.format;

import com.mikosik.stork.common.io.Input;
import com.mikosik.stork.model.Expression;

public class Stdin implements Expression {
  public final Input input;

  private Stdin(Input input) {
    this.input = input;
  }

  public static Expression stdin(Input input) {
    return new Stdin(input);
  }

  public String toString() {
    return format("stdin(#%s)", input.hashCode());
  }
}
