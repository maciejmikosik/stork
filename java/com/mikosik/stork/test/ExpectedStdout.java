package com.mikosik.stork.test;

import static com.mikosik.stork.common.io.Ascii.ascii;
import static java.lang.String.format;

import java.util.Arrays;

import org.quackery.report.AssertException;

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
      throw new AssertException(format(""
          + "expected output\n"
          + "  %s\n"
          + "but was\n"
          + "  %s\n",
          ascii(expected),
          ascii(actual)));
    }
  }
}
