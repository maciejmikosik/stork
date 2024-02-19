package com.mikosik.stork;

import static com.mikosik.stork.build.Stars.build;
import static com.mikosik.stork.build.Stars.buildCoreLibrary;
import static com.mikosik.stork.build.link.Modules.join;
import static com.mikosik.stork.build.link.problem.VerifyModule.verify;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.common.io.InputOutput.path;
import static com.mikosik.stork.common.io.Output.output;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.program.Program.program;
import static java.nio.file.FileSystems.newFileSystem;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Map;

import com.mikosik.stork.build.BuildException;
import com.mikosik.stork.model.Problem;

public class Stork {
  public static void main(String[] args) {
    try {
      var module = verify(join(
          buildCoreLibrary(pathToCoreLibraryInsideJar()),
          build(path("."))));

      program(identifier("main"), module)
          .run(input(System.in), output(System.out));
    } catch (BuildException e) {
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
