package com.mikosik.stork;

import static com.mikosik.stork.Core.core;
import static com.mikosik.stork.Core.Mode.DEVELOPMENT;
import static com.mikosik.stork.Project.project;
import static com.mikosik.stork.common.Sequence.flatten;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.common.io.Output.noOutput;
import static com.mikosik.stork.common.io.Output.output;
import static com.mikosik.stork.compile.Compiler.compile;
import static com.mikosik.stork.compile.SourceReader.sourceReader;
import static com.mikosik.stork.compile.link.VerifyLibrary.verify;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Library.library;
import static com.mikosik.stork.program.Program.program;
import static com.mikosik.stork.program.Runner.runner;
import static com.mikosik.stork.program.Task.task;
import static com.mikosik.stork.program.Terminal.terminal;

public class Demo {
  public static void main(String[] args) {
    var demoDirectory = project().demoDirectory.directory("greeting");
    var library = verify(library(flatten(
        compile(sourceReader().read(demoDirectory)),
        core(DEVELOPMENT).definitions)));

    runner().run(task(
        program(identifier("main"), library),
        terminal(input(System.in), output(System.out), noOutput())));
  }
}
