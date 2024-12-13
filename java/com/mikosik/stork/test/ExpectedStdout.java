package com.mikosik.stork.test;

import static com.mikosik.stork.common.io.Ascii.ascii;
import static com.mikosik.stork.test.QuackeryHelper.assertException;

import java.util.Arrays;

public class ExpectedStdout {
  private byte[] expected;

  private ExpectedStdout() {}

  public static ExpectedStdout expectedStdout() {
    return new ExpectedStdout();
  }

  public void expect(byte[] expected) {
    this.expected = expected;
  }

  public void verify(byte[] actual) {
    // TODO handle formatting when stdout is not ascii
    if (!Arrays.equals(actual, expected)) {
      throw assertException(""
          + "expected output\n"
          + "  %s\n"
          + "but was\n"
          + "  %s\n",
          ascii(expected),
          ascii(actual));
    }
  }
}
