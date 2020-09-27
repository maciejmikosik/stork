package com.mikosik.stork.testing;

import java.io.PrintStream;

import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.tool.Decompiler;

public class MockingDecompiler extends Decompiler {
  protected MockingDecompiler() {}

  public static Decompiler mockingDecompiler() {
    return new MockingDecompiler();
  }

  protected void print(Expression expression, PrintStream output) {
    if (expression instanceof Mock) {
      Mock mock = (Mock) expression;
      output.print(mock.toString());
    } else {
      super.print(expression, output);
    }
  }
}
