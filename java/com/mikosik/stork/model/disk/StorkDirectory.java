package com.mikosik.stork.model.disk;

import com.mikosik.stork.model.disk.StorkFile.ImportFile;
import com.mikosik.stork.model.disk.StorkFile.SourceFile;
import com.mikosik.stork.model.exp.Namespace;

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
