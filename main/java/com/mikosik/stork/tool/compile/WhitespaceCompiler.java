package com.mikosik.stork.tool.compile;

import static com.mikosik.stork.common.Ascii.isWhitespace;

import com.mikosik.stork.common.PeekingInput;

public class WhitespaceCompiler implements Compiler<Void> {
  public Void compile(PeekingInput input) {
    while (input.peek() != -1 && isWhitespace(input.peek())) {
      input.read();
    }
    return null;
  }
}
