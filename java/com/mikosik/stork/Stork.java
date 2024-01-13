package com.mikosik.stork;

import static com.mikosik.stork.common.Sequence.sequence;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.common.io.Output.output;
import static com.mikosik.stork.compile.Bind.join;
import static com.mikosik.stork.compile.CombinatoryModule.combinatoryModule;
import static com.mikosik.stork.compile.MathModule.mathModule;
import static com.mikosik.stork.compile.Stars.build;
import static com.mikosik.stork.compile.Stars.moduleFromDirectory;
import static com.mikosik.stork.compile.problem.VerifyModule.verify;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.program.Program.program;
import static com.mikosik.stork.program.ProgramModule.programModule;
import static java.nio.file.FileSystems.newFileSystem;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Map;

import com.mikosik.stork.model.Module;

public class Stork {
  public static void main(String[] args) {
    var module = build(verify(join(sequence(
        coreLibraryInsideJar(),
        moduleFromDirectory(Paths.get(".")),
        programModule(),
        combinatoryModule(),
        mathModule()))));

    program(identifier("main"), module)
        .run(input(System.in), output(System.out));
  }

  private static Module coreLibraryInsideJar() {
    try {
      // TODO why jar fs needs "core.star"?
      var jarFileSystem = newFileSystem(
          Stork.class.getClassLoader().getResource("core.star").toURI(),
          Map.of("create", "false"));
      var coreStarFileSystem = newFileSystem(jarFileSystem.getPath("/core.star"));
      return moduleFromDirectory(coreStarFileSystem.getPath("."));
    } catch (URISyntaxException | IOException e) {
      throw new LinkageError("", e);
    }
  }
}
