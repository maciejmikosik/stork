package com.mikosik.stork.program;

import com.mikosik.stork.common.io.Output;
import com.mikosik.stork.model.Expression;

public class Stdout implements Expression {
  public final Output output;

  private Stdout(Output output) {
    this.output = output;
  }

  public static Expression stdout(Output output) {
    return new Stdout(output);
  }
}
