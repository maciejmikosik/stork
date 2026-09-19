package com.mikosik.stork.test.build;

import static com.mikosik.stork.common.io.AsciiStrings.tryToAsciiBytesOrFail;
import static com.mikosik.stork.model.disk.StorkDirectory.storkDirectory;
import static com.mikosik.stork.model.exp.Namespace.root;
import static com.mikosik.stork.test.Factories.namespace;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.mikosik.stork.model.disk.StorkDirectory;
import com.mikosik.stork.model.exp.Namespace;

public class StorkDirectoryBuilder {
  private final Namespace namespace;
  private byte[] imports = new byte[0];
  private byte[] source = new byte[0];

  private StorkDirectoryBuilder(Namespace namespace) {
    this.namespace = namespace;
  }

  public static StorkDirectoryBuilder path() {
    return new StorkDirectoryBuilder(root);
  }

  public static StorkDirectoryBuilder path(String path) {
    return new StorkDirectoryBuilder(namespace(path));
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
    return source(tryToAsciiBytesOrFail(source));
  }

  public StorkDirectory build() {
    return storkDirectory(namespace, imports, source);
  }
}
