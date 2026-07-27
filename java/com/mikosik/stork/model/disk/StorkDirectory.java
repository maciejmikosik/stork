package com.mikosik.stork.model.disk;

import com.mikosik.stork.model.exp.Namespace;

public class StorkDirectory {
  public final Namespace namespace;
  public final byte[] importFile;
  public final byte[] sourceFile;

  private StorkDirectory(
      Namespace namespace,
      byte[] importFile,
      byte[] sourceFile) {
    this.namespace = namespace;
    this.importFile = importFile;
    this.sourceFile = sourceFile;
  }

  public static StorkDirectory storkDirectory(
      Namespace namespace,
      byte[] importFile,
      byte[] sourceFile) {
    return new StorkDirectory(
        namespace,
        importFile,
        sourceFile);
  }
}
