package com.mikosik.stork.common;

import static java.lang.String.format;

public class StandardOutput {
  public static void out(String template, Object... items) {
    System.out.println(format(template, items));
  }

  public static void err(String template, Object... items) {
    System.err.println(format(template, items));
  }
}
