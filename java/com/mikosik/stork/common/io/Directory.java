package com.mikosik.stork.common.io;

import static com.mikosik.stork.common.Throwables.check;
import static com.mikosik.stork.common.io.InputOutput.unchecked;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

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

  public Stream<Directory> directories() {
    return list()
        .filter(Files::isDirectory)
        .map(Directory::directory);
  }

  public Stream<File> files() {
    return list()
        .filter(Files::isRegularFile)
        .map(File::file);
  }

  private Stream<Path> list() {
    try {
      return Files.list(path);
    } catch (IOException e) {
      throw unchecked(e);
    }
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

  public Directory delete() {
    try {
      Files.delete(path);
    } catch (IOException e) {
      throw unchecked(e);
    }
    return this;
  }

  public Directory deleteRecursively() {
    directories().forEach(Directory::deleteRecursively);
    files().forEach(File::delete);
    delete();
    return this;
  }

  public String toString() {
    return path.toString();
  }
}
