package com.mikosik.stork.program;

import com.mikosik.stork.common.io.Input;
import com.mikosik.stork.model.Expression;

public class Stdin implements Expression {
  public final Input input;
  public final int index;

  private Stdin(Input input, int index) {
    this.input = input;
    this.index = index;
  }

  public static Expression stdin(Input input) {
    return new Stdin(input, 0);
  }

  public static Expression stdin(Input input, int index) {
    return new Stdin(input, index);
  }
}
