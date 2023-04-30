package com.mikosik.stork.demo;

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

import java.nio.file.Paths;

import com.mikosik.stork.model.Module;
import com.mikosik.stork.program.Program;

public class RunDemo {
  public static void main(String[] args) {
    Module module = build(verify(join(sequence(
        moduleFromDirectory(Paths.get("demo/com/mikosik/stork/demo")),
        moduleFromDirectory(Paths.get("core_star")),
        programModule(),
        combinatoryModule(),
        mathModule()))));

    Program program = program(identifier("main"), module);
    program.run(input(System.in), output(System.out));
  }
}
