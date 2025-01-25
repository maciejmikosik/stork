package com.mikosik.stork.common.io;

import static com.mikosik.stork.common.io.Directory.directory;
import static com.mikosik.stork.common.io.InputOutput.unchecked;

import java.io.IOException;
import java.nio.file.FileSystems;
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
    var fileSystem = FileSystems.getDefault();
    var userHome = System.getProperty("user.home");
    return directory(fileSystem.getPath(userHome));
  }

  public static Directory workingDirectory() {
    var fileSystem = FileSystems.getDefault();
    var path = fileSystem.getPath(".").toAbsolutePath();
    return directory(path);
  }

  public static Directory systemTemporaryDirectory() {
    return directory(Path.of(System.getProperty("java.io.tmpdir")));
  }
}
