package com.mikosik.stork;

import static com.mikosik.stork.Project.project;
import static com.mikosik.stork.common.ImmutableList.join;
import static com.mikosik.stork.common.Throwables.linkageError;
import static com.mikosik.stork.common.io.Directory.directory;
import static com.mikosik.stork.compile.Compiler.compile;
import static com.mikosik.stork.compile.SourceReader.sourceReader;
import static com.mikosik.stork.compile.link.OperatorLibrary.operatorLibrary;
import static com.mikosik.stork.compile.link.VerifyLibrary.verify;
import static java.nio.file.FileSystems.newFileSystem;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import com.mikosik.stork.common.io.Directory;
import com.mikosik.stork.model.Definition;

public class Core {
  public static enum Mode {
    /** launched from eclipse or from bash script */
    TESTING,
    /** launched from eclipse or from bash script */
    DEVELOPMENT,
    /** launched using stork binary */
    PRODUCTION,
  }

  public static List<Definition> core(Mode mode) {
    return verify(join(
        compile(sourceReader().read(coreDirectoryFor(mode))),
        operatorLibrary()));
  }

  private static Directory coreDirectoryFor(Mode mode) {
    return switch (mode) {
      case TESTING -> project().mincoreDirectory;
      case DEVELOPMENT -> project().coreDirectory;
      case PRODUCTION -> coreDirectoryInZipFileInJarFile();
    };
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
