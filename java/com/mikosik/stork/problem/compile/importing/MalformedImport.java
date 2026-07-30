package com.mikosik.stork.problem.compile.importing;

public class MalformedImport extends CannotImport {
  public final String text;

  private MalformedImport(String text) {
    this.text = text;
  }

  public static MalformedImport malformedImport(String text) {
    return new MalformedImport(text);
  }
}
