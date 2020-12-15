package com.mikosik.stork.common;

public class PeekingInput {
  private final Input input;
  private int next;

  private PeekingInput(Input input, int next) {
    this.input = input;
    this.next = next;
  }

  public static PeekingInput peeking(Input input) {
    return new PeekingInput(input, input.read());
  }

  public int peek() {
    return next;
  }

  public int read() {
    int result = next;
    next = input.read();
    return result;
  }
}
