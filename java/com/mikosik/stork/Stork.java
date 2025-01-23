package com.mikosik.stork;

import static com.mikosik.stork.CoreLibrary.coreLibrary;
import static com.mikosik.stork.CoreLibrary.Mode.PRODUCTION;
import static com.mikosik.stork.common.io.Directories.workingDirectory;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.common.io.Output.output;
import static com.mikosik.stork.compile.Compilation.compilation;
import static com.mikosik.stork.compile.Compiler.compile;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.program.Program.program;

import com.mikosik.stork.problem.Problem;
import com.mikosik.stork.problem.ProblemException;

public class Stork {
  public static void main(String[] args) {
    try {
      var library = compile(compilation()
          .source(workingDirectory())
          .library(coreLibrary(PRODUCTION)));

      program(identifier("main"), library)
          .run(input(System.in), output(System.out));
    } catch (ProblemException e) {
      for (Problem problem : e.problems) {
        System.err.println(problem.description());
      }
    }
  }
}
