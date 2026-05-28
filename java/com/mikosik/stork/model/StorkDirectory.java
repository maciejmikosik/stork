package com.mikosik.stork.model;

import com.mikosik.stork.model.StorkFile.ImportFile;
import com.mikosik.stork.model.StorkFile.SourceFile;

public class StorkDirectory {
  public final ImportFile importFile;
  public final SourceFile sourceFile;

  public static StorkDirectory storkDirectory(
      ImportFile importFile,
      SourceFile sourceFile) {
    return new StorkDirectory(
        importFile,
        sourceFile);
  }

  private StorkDirectory(
      ImportFile importFile,
      SourceFile sourceFile) {
    this.importFile = importFile;
    this.sourceFile = sourceFile;
  }
}
