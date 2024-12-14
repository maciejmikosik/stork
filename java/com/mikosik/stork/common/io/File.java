package com.mikosik.stork.common.io;

import static com.mikosik.stork.common.Throwables.check;
import static com.mikosik.stork.common.io.InputOutput.unchecked;
import static java.nio.file.StandardOpenOption.APPEND;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;

public class File {
  public final Path path;

  private File(Path path) {
    this.path = path;
  }

  public static File file(Path path) {
    check(path.isAbsolute());
    return new File(path);
  }

  public String name() {
    return path.getFileName().toString();
  }

  public boolean exists() {
    return Files.isRegularFile(path);
  }

  public byte[] readAllBytes() {
    try {
      return Files.readAllBytes(path);
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  public File create() {
    try {
      Files.createFile(path);
      return this;
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  public File createDeep() {
    try {
      Files.createDirectories(path.getParent());
      Files.createFile(path);
      return this;
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  public File append(byte[] bytes) {
    try {
      Files.write(path, bytes, APPEND);
      return this;
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  public File append(File file) {
    return append(file.readAllBytes());
  }

  public File addPermission(PosixFilePermission permission) {
    try {
      var permissions = Files.getPosixFilePermissions(path);
      permissions.add(permission);
      Files.setPosixFilePermissions(path, permissions);
      return this;
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  public File move(Directory directory) {
    try {
      var target = directory.file(name()).path;
      Files.move(path, target);
      return file(target);
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  public File delete() {
    try {
      check(exists());
      Files.delete(path);
      return this;
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  public Input input() {
    try {
      return Input.input(Files.newInputStream(path));
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  public Input tryInput() {
    return exists()
        ? Input.input(path)
        : Input.input(new byte[0]);
  }

  public String toString() {
    return path.toString();
  }
}
