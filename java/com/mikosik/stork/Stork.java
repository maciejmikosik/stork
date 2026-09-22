package com.mikosik.stork;

import static com.mikosik.stork.Core.core;
import static com.mikosik.stork.Core.Mode.PRODUCTION;
import static com.mikosik.stork.common.io.Directories.workingDirectory;
import static com.mikosik.stork.common.io.Directory.directory;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.common.io.Output.output;
import static com.mikosik.stork.compile.Codebase.codebase;
import static com.mikosik.stork.compile.Compiler.compile;
import static com.mikosik.stork.compile.SourceReader.SOURCE_FILENAME;
import static com.mikosik.stork.compile.SourceReader.sourceReader;
import static com.mikosik.stork.compile.err.Describer.describe;
import static com.mikosik.stork.model.disk.StorkDirectory.storkDirectory;
import static com.mikosik.stork.model.exp.Identifier.identifier;
import static com.mikosik.stork.model.exp.Namespace.namespaceRoot;
import static com.mikosik.stork.model.exp.Variable.variable;
import static com.mikosik.stork.program.Program.program;
import static com.mikosik.stork.program.Runner.runner;
import static com.mikosik.stork.program.Task.task;
import static com.mikosik.stork.program.Terminal.terminal;
import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.FileDescriptor;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;

import com.mikosik.stork.compile.err.CompilerException;
import com.mikosik.stork.compute.err.ComputerException;
import com.mikosik.stork.model.disk.StorkDirectory;

public class Stork {
  public static void main(String[] args) {
    try {
      if (args.length == 1) {
        runSnippet(args[0]);
      } else {
        runMain();
      }
    } catch (CompilerException compilerException) {
      System.err.println(describe(compilerException));
      System.exit(1);
    } catch (ComputerException computerException) {
      System.err.println(computerException.problem.toOutline());
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

  private static void runMain() {
    var workingDirectory = workingDirectory();
    var mainSourceFile = workingDirectory.file(SOURCE_FILENAME);
    if (!mainSourceFile.exists()) {
      System.err.println("file %s does not exist"
          .formatted(mainSourceFile));
      System.exit(1);
    }
    var library = compile(codebase()
        .directories(sourceReader().read(workingDirectory))
        .dependencies(core(PRODUCTION))
        .build());
    runner().run(task(
        program(identifier(variable("main")), library),
        terminal(input(System.in), output(FileDescriptor.out))));
    System.exit(0);
  }

  private static void runSnippet(String snippet) {
    var envKey = "STORK_PATH";
    var envValue = System.getenv(envKey);
    if (envValue == null) {
      System.err.println("%s is not defined".formatted(envKey));
      System.exit(1);
    }
    var storkDirectoryPath = Path.of(envValue);
    if (!storkDirectoryPath.isAbsolute()) {
      System.err.println("%s=%s is not absolute"
          .formatted(envKey, envValue));
      System.exit(1);
    }
    var storkDirectory = directory(storkDirectoryPath);
    if (!storkDirectory.exists()) {
      System.err.println("%s does not exist".formatted(storkDirectory));
      System.exit(1);
    }

    var library = compile(codebase()
        .directories(injectSnippet(snippet, sourceReader().read(storkDirectory)))
        .dependencies(core(PRODUCTION))
        .build());
    runner().run(task(
        program(identifier(variable("main")), library),
        terminal(input(System.in), output(FileDescriptor.out))));
    System.exit(0);
  }

  private static List<StorkDirectory> injectSnippet(
      String snippet,
      List<StorkDirectory> directories) {
    return directories.stream()
        .map(directory -> directory.namespace.equals(namespaceRoot())
            ? storkDirectory(
                directory.namespace,
                directory.importFile,
                injectSnippet(snippet, directory.sourceFile))
            : directory)
        .toList();
  }

  private static byte[] injectSnippet(
      String snippet,
      byte[] sourceFile) {
    return "%s\n\n    main { %s }".formatted(
        new String(sourceFile, US_ASCII),
        snippet)
        .getBytes(US_ASCII);
  }

  private static boolean isMessage(
      String message,
      UncheckedIOException exception) {
    return message.equals(exception.getCause().getMessage());
  }
}
