package com.mikosik.stork.common.io;

import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.common.io.InputOutput.unchecked;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Directory {
  public final Path path;

  private Directory(Path path) {
    this.path = path;
  }

  public static Directory directory(Path path) {
    check(path.isAbsolute());
    return new Directory(path);
  }

  public Directory directory(String name) {
    return directory(path.resolve(name));
  }

  public File file(String name) {
    return File.file(path.resolve(name));
  }

  public Directory parent() {
    return directory(path.getParent());
  }

  public boolean exists() {
    return Files.isDirectory(path);
  }

  public Directory create() {
    try {
      Files.createDirectory(path);
      return this;
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  public String toString() {
    return path.toString();
  }
}
