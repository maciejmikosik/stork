package com.mikosik.stork;

import static com.mikosik.stork.Core.core;
import static com.mikosik.stork.Core.Mode.PRODUCTION;
import static com.mikosik.stork.common.io.Directories.workingDirectory;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.common.io.Output.output;
import static com.mikosik.stork.compile.Compilation.compilation;
import static com.mikosik.stork.compile.Compiler.compile;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.program.Runner.runner;

import com.mikosik.stork.problem.Problem;

public class Stork {
  public static void main(String[] args) {
    try {
      var library = compile(compilation()
          .source(workingDirectory())
          .library(core(PRODUCTION)));

      runner(identifier("main"), library)
          .run(input(System.in), output(System.out));
      System.exit(0);
    } catch (Problem problem) {
      System.err.println(problem);
      System.exit(1);
    }
  }
}
