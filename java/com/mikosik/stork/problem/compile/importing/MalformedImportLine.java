package com.mikosik.stork.problem.compile.importing;

import com.mikosik.stork.model.exp.Namespace;

public class MalformedImportLine extends CannotImport {
  public final Namespace namespace;
  public final String line;

  protected MalformedImportLine(
      Namespace namespace,
      String line) {
    this.namespace = namespace;
    this.line = line;
  }

  public static MalformedImportLine malformedImportLine(
      Namespace namespace,
      String line) {
    return new MalformedImportLine(namespace, line);
  }
}
