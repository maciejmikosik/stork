package com.mikosik.stork;

import static com.mikosik.stork.Core.core;
import static com.mikosik.stork.Core.Mode.PRODUCTION;
import static com.mikosik.stork.common.io.Directories.workingDirectory;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.common.io.Output.output;
import static com.mikosik.stork.compile.Codebase.codebase;
import static com.mikosik.stork.compile.Compiler.compile;
import static com.mikosik.stork.compile.SourceReader.sourceReader;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.problem.Describe.describe;
import static com.mikosik.stork.program.Program.program;
import static com.mikosik.stork.program.Runner.runner;
import static com.mikosik.stork.program.Task.task;
import static com.mikosik.stork.program.Terminal.terminal;

import java.io.FileDescriptor;
import java.io.UncheckedIOException;

import com.mikosik.stork.problem.compile.CannotCompile;
import com.mikosik.stork.problem.compute.CannotCompute;

public class Stork {
  public static void main(String[] args) {
    try {
      var library = compile(codebase()
          .directories(sourceReader().read(workingDirectory()))
          .dependencies(core(PRODUCTION))
          .build());
      runner().run(task(
          program(identifier("main"), library),
          terminal(input(System.in), output(FileDescriptor.out))));
      System.exit(0);
    } catch (CannotCompile cannotCompile) {
      System.err.println(describe(cannotCompile));
      System.exit(1);
    } catch (CannotCompute cannotCompute) {
      System.err.println(describe(cannotCompute));
      System.exit(1);
    } catch (UncheckedIOException e) {
      if (isMessage("Broken pipe", e)) {
        var sig = 128;
        var pipe = 13;
        System.exit(sig + pipe);
      } else {
        throw e;
      }
    }
  }

  private static boolean isMessage(
      String message,
      UncheckedIOException exception) {
    return message.equals(exception.getCause().getMessage());
  }
}
