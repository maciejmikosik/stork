package com.mikosik.stork.model;

public sealed class StorkFile {

  public static final class ImportFile extends StorkFile {
    public static final String FILE_NAME = "import.stork";

    public final Namespace namespace;
    public final byte[] content;

    public ImportFile(Namespace namespace, byte[] content) {
      this.namespace = namespace;
      this.content = content;
    }

    public static ImportFile importFile(Namespace namespace, byte[] content) {
      return new ImportFile(namespace, content);
    }
  }

  public static final class SourceFile extends StorkFile {
    public static final String FILE_NAME = "source.stork";

    public final Namespace namespace;
    public final byte[] content;

    public SourceFile(Namespace namespace, byte[] content) {
      this.namespace = namespace;
      this.content = content;
    }

    public static SourceFile sourceFile(Namespace namespace, byte[] content) {
      return new SourceFile(namespace, content);
    }
  }
}
