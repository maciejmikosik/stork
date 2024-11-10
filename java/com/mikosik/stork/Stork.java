package com.mikosik.stork;

import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.common.io.InputOutput.path;
import static com.mikosik.stork.common.io.Output.output;
import static com.mikosik.stork.compile.Compiler.compileCoreLibrary;
import static com.mikosik.stork.compile.Compiler.compileDirectory;
import static com.mikosik.stork.compile.link.Modules.join;
import static com.mikosik.stork.compile.link.VerifyModule.verify;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.program.Program.program;
import static java.nio.file.FileSystems.newFileSystem;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Map;

import com.mikosik.stork.problem.Problem;
import com.mikosik.stork.problem.ProblemException;

public class Stork {
  public static void main(String[] args) {
    try {
      var module = verify(join(
          compileCoreLibrary(pathToCoreLibraryInsideJar()),
          compileDirectory(path("."))));

      program(identifier("main"), module)
          .run(input(System.in), output(System.out));
    } catch (ProblemException e) {
      for (Problem problem : e.problems) {
        System.err.println(problem.description());
      }
    }
  }

  private static Path pathToCoreLibraryInsideJar() {
    try {
      // TODO why jar fs needs "core.star"?
      var jarFileSystem = newFileSystem(
          Stork.class.getClassLoader().getResource("core.star").toURI(),
          Map.of("create", "false"));
      var coreStarFileSystem = newFileSystem(jarFileSystem.getPath("/core.star"));
      return coreStarFileSystem.getPath(".");
    } catch (URISyntaxException | IOException e) {
      throw new LinkageError("", e);
    }
  }
}
