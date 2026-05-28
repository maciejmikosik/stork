package com.mikosik.stork.program;

import com.mikosik.stork.common.io.Input;
import com.mikosik.stork.common.io.Output;

public class Terminal {
  public final Input input;
  public final Output output;

  private Terminal(Input input, Output output) {
    this.input = input;
    this.output = output;
  }

  public static Terminal terminal(Input input, Output output) {
    return new Terminal(input, output);
  }
}
