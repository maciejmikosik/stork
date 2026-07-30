package com.mikosik.stork.problem.compile.importing;

public class MalformedImportLine extends CannotImport {
  public final String line;

  protected MalformedImportLine(String line) {
    this.line = line;
  }

  public static MalformedImportLine malformedImportLine(String line) {
    return new MalformedImportLine(line);
  }
}
