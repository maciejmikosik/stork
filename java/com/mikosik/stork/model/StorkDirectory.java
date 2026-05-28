package com.mikosik.stork.model;

import com.mikosik.stork.model.StorkFile.ImportFile;
import com.mikosik.stork.model.StorkFile.SourceFile;

public class StorkDirectory {
  public final Namespace namespace;
  public final ImportFile importFile;
  public final SourceFile sourceFile;

  private StorkDirectory(
      Namespace namespace,
      ImportFile importFile,
      SourceFile sourceFile) {
    this.namespace = namespace;
    this.importFile = importFile;
    this.sourceFile = sourceFile;
  }

  public static StorkDirectory storkDirectory(
      Namespace namespace,
      ImportFile importFile,
      SourceFile sourceFile) {
    return new StorkDirectory(
        namespace,
        importFile,
        sourceFile);
  }
}
