package com.mikosik.stork.model;

public class StorkFile {
  public final Kind kind;
  public final Namespace namespace;
  public final byte[] content;

  private StorkFile(
      Kind kind,
      Namespace namespace,
      byte[] content) {
    this.kind = kind;
    this.namespace = namespace;
    this.content = content;
  }

  public static StorkFile storkFile(
      Kind kind,
      Namespace namespace,
      byte[] content) {
    return new StorkFile(kind, namespace, content);
  }

  public enum Kind {
    CODE("source.stork"),
    IMPORT("import.stork");

    public final String fileName;

    Kind(String fileName) {
      this.fileName = fileName;
    }
  }
}
