package com.mikosik.stork;

import static com.mikosik.stork.Core.core;
import static com.mikosik.stork.Core.Mode.PRODUCTION;
import static com.mikosik.stork.common.ImmutableList.join;
import static com.mikosik.stork.common.io.Directories.workingDirectory;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.common.io.Output.noOutput;
import static com.mikosik.stork.common.io.Output.output;
import static com.mikosik.stork.compile.Compiler.compile;
import static com.mikosik.stork.compile.SourceReader.sourceReader;
import static com.mikosik.stork.compile.link.VerifyLibrary.verify;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.program.Program.program;
import static com.mikosik.stork.program.Runner.runner;
import static com.mikosik.stork.program.Task.task;
import static com.mikosik.stork.program.Terminal.terminal;

import com.mikosik.stork.problem.Problem;

public class Stork {
  public static void main(String[] args) {
    try {
      var library = verify(join(
          compile(sourceReader().read(workingDirectory())),
          core(PRODUCTION)));

      runner().run(task(
          program(identifier("main"), library),
          terminal(input(System.in), output(System.out), noOutput())));

      System.exit(0);
    } catch (Problem problem) {
      System.err.println(problem);
      System.exit(1);
    }
  }
}
