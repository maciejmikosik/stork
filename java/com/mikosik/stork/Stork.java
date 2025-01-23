package com.mikosik.stork;

import static com.mikosik.stork.common.Throwables.linkageError;
import static com.mikosik.stork.common.io.Directories.workingDirectory;
import static com.mikosik.stork.common.io.Directory.directory;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.common.io.Output.output;
import static com.mikosik.stork.compile.Compilation.compilation;
import static com.mikosik.stork.compile.Compiler.compile;
import static com.mikosik.stork.compile.Compiler.nativeLibrary;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.program.Program.program;
import static java.nio.file.FileSystems.newFileSystem;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import com.mikosik.stork.common.io.Directory;
import com.mikosik.stork.problem.Problem;
import com.mikosik.stork.problem.ProblemException;

public class Stork {
  public static void main(String[] args) {
    try {
      var library = compile(compilation()
          .source(workingDirectory())
          .source(coreLibraryDirectoryInZipFileInJarFile())
          .library(nativeLibrary()));

      program(identifier("main"), library)
          .run(input(System.in), output(System.out));
    } catch (ProblemException e) {
      for (Problem problem : e.problems) {
        System.err.println(problem.description());
      }
    }
  }

  // TODO simplify to directory inside jar
  private static Directory coreLibraryDirectoryInZipFileInJarFile() {
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
