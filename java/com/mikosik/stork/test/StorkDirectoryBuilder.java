package com.mikosik.stork.test;

import static com.mikosik.stork.model.Namespace.namespaceOf;
import static com.mikosik.stork.model.StorkDirectory.storkDirectory;
import static com.mikosik.stork.model.StorkFile.ImportFile.importFile;
import static com.mikosik.stork.model.StorkFile.SourceFile.sourceFile;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.mikosik.stork.model.Namespace;
import com.mikosik.stork.model.StorkDirectory;

public class StorkDirectoryBuilder {
  private Namespace namespace = namespaceOf();
  private byte[] imports = new byte[0];
  private byte[] source = new byte[0];

  public StorkDirectoryBuilder path(String path) {
    this.namespace = namespaceOf(path.split("/"));
    return this;
  }

  public StorkDirectoryBuilder imports(String imports) {
    this.imports = imports.getBytes(UTF_8);
    return this;
  }

  public StorkDirectoryBuilder sourceRaw(byte[] source) {
    this.source = source;
    return this;
  }

  public StorkDirectoryBuilder sourceRaw(String source) {
    return sourceRaw(source.getBytes(UTF_8));
  }

  public StorkDirectoryBuilder source(String source) {
    return sourceRaw(source.replace('\'', '\"'));
  }

  public StorkDirectory build() {
    return storkDirectory(
        importFile(namespace, imports),
        sourceFile(namespace, source));
  }

  public static class Factory {
    public static StorkDirectoryBuilder path() {
      return new StorkDirectoryBuilder();
    }

    public static StorkDirectoryBuilder path(String path) {
      return new StorkDirectoryBuilder().path(path);
    }

    public static StorkDirectoryBuilder imports(String imports) {
      return new StorkDirectoryBuilder().imports(imports);
    }

    public static StorkDirectoryBuilder sourceRaw(String source) {
      return new StorkDirectoryBuilder().sourceRaw(source);
    }

    public static StorkDirectoryBuilder source(String source) {
      return new StorkDirectoryBuilder().source(source);
    }
  }
}
