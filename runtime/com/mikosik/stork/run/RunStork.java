package com.mikosik.stork.run;

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
import static com.mikosik.stork.model.Module.module;
import static com.mikosik.stork.program.Program.program;
import static com.mikosik.stork.program.ProgramModule.programModule;
import static com.mikosik.stork.run.ParseOptions.parseOptions;
import static java.util.Collections.EMPTY_LIST;

import java.nio.file.Paths;

import com.mikosik.stork.model.Module;

public class RunStork {
  public static void main(String[] args) {
    var options = parseOptions(sequence(args));

    if (options.containsKey("--version")) {
      System.out.println("stork version 0.1");
      return;
    }

    if (!options.containsKey("--source")) {
      fail("missing option --source");
    }
    var sourceDirectory = Paths.get(options.get("--source").get(0));
    var includesCore = options.containsKey("--core");
    var mainFunction = options.containsKey("--main")
        ? options.get("--main").get(0)
        : "main";

    Module module = build(verify(join(sequence(
        moduleFromDirectory(sourceDirectory),
        includesCore
            ? moduleFromDirectory(Paths.get("core_star"))
            : module(EMPTY_LIST),
        programModule(),
        combinatoryModule(),
        mathModule()))));

    program(identifier(mainFunction), module)
        .run(input(System.in), output(System.out));
  }

  private static void fail(String message) {
    System.err.println(message);
    System.exit(1);
  }
}
