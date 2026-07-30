package com.mikosik.stork.problem.compile.importing;

import java.util.List;

import com.mikosik.stork.model.exp.Namespace;

public class MalformedImportFile extends CannotImport {
  public final Namespace namespace;
  public final List<MalformedImportLine> problems;

  private MalformedImportFile(
      Namespace namespace,
      List<MalformedImportLine> problems) {
    this.namespace = namespace;
    this.problems = problems;
  }

  public static MalformedImportFile malformedImportFile(
      Namespace namespace,
      List<MalformedImportLine> problems) {
    return new MalformedImportFile(
        namespace,
        problems);
  }
}
