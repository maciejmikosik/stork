package com.mikosik.stork.common.io;

import static com.mikosik.stork.common.io.Directory.directory;
import static com.mikosik.stork.common.io.InputOutput.unchecked;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Directories {
  public static Directory newTemporaryDirectory(String prefix) {
    try {
      return deleteOnExit(directory(Files.createTempDirectory(prefix)));
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  private static Directory deleteOnExit(Directory directory) {
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      directory.deleteRecursively();
    }));
    return directory;
  }

  public static Directory homeDirectory() {
    return directoryFromProperty("user.home");
  }

  public static Directory workingDirectory() {
    return directoryFromProperty("user.dir");
  }

  public static Directory systemTemporaryDirectory() {
    return directoryFromProperty("java.io.tmpdir");
  }

  private static Directory directoryFromProperty(String key) {
    return directory(Path.of(System.getProperty(key)));
  }
}
