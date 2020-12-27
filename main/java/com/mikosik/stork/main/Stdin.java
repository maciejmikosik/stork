package com.mikosik.stork.main;

import static java.lang.String.format;

import com.mikosik.stork.common.Input;
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
