package com.mikosik.stork.demo;

import static com.mikosik.stork.common.Chain.chain;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.common.io.Output.output;
import static com.mikosik.stork.compile.Bind.join;
import static com.mikosik.stork.compile.Stars.build;
import static com.mikosik.stork.compile.Stars.langModule;
import static com.mikosik.stork.compile.Stars.moduleFromDirectory;
import static com.mikosik.stork.compile.Stars.verify;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.program.Program.program;
import static com.mikosik.stork.program.ProgramModule.programModule;

import java.nio.file.Paths;

import com.mikosik.stork.model.Module;
import com.mikosik.stork.program.Program;

public class RunDemo {
  public static void main(String[] args) {
    Module module = build(verify(join(chain(
        moduleFromDirectory(Paths.get("demo/com/mikosik/stork/demo")),
        programModule(),
        langModule()))));

    Program program = program(identifier("main"), module);
    program.run(input(System.in), output(System.out));
  }
}
