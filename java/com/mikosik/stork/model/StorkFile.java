package com.mikosik.stork.model;

public sealed class StorkFile {

  public static final class ImportFile extends StorkFile {
    public final byte[] content;

    public ImportFile(byte[] content) {
      this.content = content;
    }

    public static ImportFile importFile(byte[] content) {
      return new ImportFile(content);
    }
  }

  public static final class SourceFile extends StorkFile {
    public final byte[] content;

    public SourceFile(byte[] content) {
      this.content = content;
    }

    public static SourceFile sourceFile(byte[] content) {
      return new SourceFile(content);
    }
  }
}
