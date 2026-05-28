package com.mikosik.stork.test;

import static com.mikosik.stork.model.Namespace.namespaceOf;
import static com.mikosik.stork.model.StorkDirectory.storkDirectory;
import static com.mikosik.stork.model.StorkFile.ImportFile.importFile;
import static com.mikosik.stork.model.StorkFile.SourceFile.sourceFile;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.mikosik.stork.model.Namespace;
import com.mikosik.stork.model.StorkDirectory;

public class StorkDirectoryBuilder {
  private final Namespace namespace;
  private byte[] imports = new byte[0];
  private byte[] source = new byte[0];

  private StorkDirectoryBuilder(Namespace namespace) {
    this.namespace = namespace;
  }

  public static StorkDirectoryBuilder path() {
    return new StorkDirectoryBuilder(namespaceOf());
  }

  public static StorkDirectoryBuilder path(String path) {
    return new StorkDirectoryBuilder(namespaceOf(path.split("/")));
  }

  public StorkDirectoryBuilder imports(String imports) {
    this.imports = imports.getBytes(UTF_8);
    return this;
  }

  public StorkDirectoryBuilder source(byte[] source) {
    this.source = source;
    return this;
  }

  public StorkDirectoryBuilder source(String source) {
    return source(source
        .replace('\'', '\"')
        .getBytes(UTF_8));
  }

  public StorkDirectory build() {
    return storkDirectory(
        importFile(namespace, imports),
        sourceFile(namespace, source));
  }
}
