package com.mikosik.stork.test;

import static com.mikosik.stork.common.io.InputOutput.createDirectories;
import static com.mikosik.stork.common.io.InputOutput.deleteRecursively;
import static com.mikosik.stork.common.io.Output.output;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.nio.file.Path;

// TODO implement some kind of in-memory filesystem
public class FsBuilder {
  public final Path directory;

  private FsBuilder(Path directory) {
    this.directory = directory;
  }

  public static FsBuilder fsBuilder(Path directory) {
    return new FsBuilder(directory);
  }

  public FsBuilder file(String path, String content) {
    return file(directory.resolve(path), content);
  }

  private FsBuilder file(Path path, String content) {
    createDirectories(path.getParent());
    output(path).write(bytes(content));
    return this;
  }

  public FsBuilder sourceFile(String content) {
    return file("source", content);
  }

  public FsBuilder importFile(String content) {
    return file("import", content);
  }

  public FsBuilder delete() {
    deleteRecursively(directory);
    return this;
  }

  private byte[] bytes(String string) {
    return string.getBytes(UTF_8);
  }
}
