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
import static com.mikosik.stork.model.Namespace.namespaceOf;
import static com.mikosik.stork.model.StorkDirectory.storkDirectory;
import static com.mikosik.stork.model.StorkFile.ImportFile.importFile;
import static com.mikosik.stork.model.StorkFile.SourceFile.sourceFile;
import static com.mikosik.stork.model.change.Changes.deep;
import static com.mikosik.stork.model.change.Changes.ifVariable;
import static com.mikosik.stork.model.change.Changes.onBody;
import static com.mikosik.stork.problem.Describe.describe;
import static com.mikosik.stork.program.Program.program;
import static com.mikosik.stork.program.Runner.runner;
import static com.mikosik.stork.program.Task.task;
import static com.mikosik.stork.program.Terminal.terminal;
import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.FileDescriptor;
import java.io.UncheckedIOException;
import java.util.List;

import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.StorkDirectory;
import com.mikosik.stork.problem.compile.CannotCompile;
import com.mikosik.stork.problem.compute.CannotCompute;

public class Stork {
  public static void main(String[] args) {
    boolean isSnippet = args.length == 1;

    try {
      var codebase = codebase()
          .directories(sourceReader().read(workingDirectory()))
          .dependencies(core(PRODUCTION));
      if (isSnippet) {
        codebase = codebase
            .dependencies(bind(compile(snippet(args[0]))));
      }
      var library = compile(codebase.build());
      runner().run(task(
          program(
              isSnippet ? identifier("snippet") : identifier("main"),
              library),
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

  private static StorkDirectory snippet(String code) {
    return storkDirectory(
        namespaceOf(),
        importFile(new byte[0]),
        sourceFile("snippet { %s }".formatted(code).getBytes(US_ASCII)));
  }

  private static List<Definition> bind(List<Definition> library) {
    return library.stream()
        .map(onBody(deep(ifVariable(Identifier::identifier))))
        .toList();
  }
}
