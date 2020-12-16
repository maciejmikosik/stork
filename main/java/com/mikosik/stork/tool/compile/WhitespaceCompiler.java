package com.mikosik.stork.tool.compile;

import static com.mikosik.stork.common.Ascii.isWhitespace;

import com.mikosik.stork.common.Input;

public class WhitespaceCompiler implements Compiler<Void> {
  public Void compile(Input input) {
    while (input.peek() != -1 && isWhitespace(input.peek())) {
      input.read();
    }
    return null;
  }
}
