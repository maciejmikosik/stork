package com.mikosik.stork;

import static com.mikosik.stork.Project.project;
import static com.mikosik.stork.common.Throwables.linkageError;
import static com.mikosik.stork.common.io.Directory.directory;
import static com.mikosik.stork.compile.Compilation.compilation;
import static com.mikosik.stork.compile.Compiler.compile;
import static com.mikosik.stork.compile.link.OperatorLibrary.operatorLibrary;
import static java.nio.file.FileSystems.newFileSystem;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import com.mikosik.stork.common.io.Directory;
import com.mikosik.stork.model.Library;

public class Core {
  public static enum Mode {
    /** launched from eclipse or from bash script */
    TESTING,
    /** launched from eclipse or from bash script */
    DEVELOPMENT,
    /** launched using stork binary */
    PRODUCTION,
  }

  public static Library core(Mode mode) {
    return compile(compilation()
        .source(switch (mode) {
          case TESTING -> project().mincoreDirectory;
          case DEVELOPMENT -> project().coreDirectory;
          case PRODUCTION -> coreDirectoryInZipFileInJarFile();
        })
        .library(operatorLibrary()));
  }

  // TODO simplify to directory inside jar
  private static Directory coreDirectoryInZipFileInJarFile() {
    try {
      // TODO why jar fs needs "core.star"?
      var jarFileSystem = newFileSystem(
          Stork.class.getClassLoader().getResource("core.star").toURI(),
          Map.of("create", "false"));
      var coreStarFileSystem = newFileSystem(jarFileSystem.getPath("/core.star"));
      return directory(coreStarFileSystem.getPath("/"));
    } catch (URISyntaxException | IOException e) {
      throw linkageError(e);
    }
  }
}
