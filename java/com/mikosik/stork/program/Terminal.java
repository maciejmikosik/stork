package com.mikosik.stork.program;

import com.mikosik.stork.common.io.Input;
import com.mikosik.stork.common.io.Output;

public class Terminal {
  public final Input input;
  public final Output output;
  public final Output error;

  private Terminal(Input input, Output output, Output error) {
    this.input = input;
    this.output = output;
    this.error = error;
  }

  public static Terminal terminal(Input input, Output output, Output error) {
    return new Terminal(input, output, error);
  }
}
