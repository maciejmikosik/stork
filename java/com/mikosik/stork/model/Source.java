package com.mikosik.stork.model;

public class Source {
  public final Kind kind;
  public final Namespace namespace;
  public final byte[] content;

  private Source(
      Kind kind,
      Namespace namespace,
      byte[] content) {
    this.kind = kind;
    this.namespace = namespace;
    this.content = content;
  }

  public static Source source(
      Kind kind,
      Namespace namespace,
      byte[] content) {
    return new Source(kind, namespace, content);
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
