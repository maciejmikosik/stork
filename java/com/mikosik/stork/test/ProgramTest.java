package com.mikosik.stork.test;

import static com.mikosik.stork.Core.Mode.DEVELOPMENT;
import static com.mikosik.stork.Core.Mode.TESTING;
import static com.mikosik.stork.common.Logic.singleton;
import static com.mikosik.stork.common.Reserver.reserver;
import static com.mikosik.stork.common.Throwables.runtimeException;
import static com.mikosik.stork.common.io.Buffer.newBuffer;
import static com.mikosik.stork.common.io.Directories.systemTemporaryDirectory;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.compile.Compilation.compilation;
import static com.mikosik.stork.compile.Compiler.compile;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.program.Program.program;
import static com.mikosik.stork.test.ExpectedProblems.expectedProblems;
import static com.mikosik.stork.test.ExpectedStdout.expectedStdout;
import static com.mikosik.stork.test.FsBuilder.fsBuilder;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.UUID.randomUUID;
import static org.quackery.Case.newCase;

import java.util.function.Supplier;

import org.quackery.Test;

import com.mikosik.stork.Core;
import com.mikosik.stork.common.Reserver;
import com.mikosik.stork.common.io.Directory;
import com.mikosik.stork.model.Library;
import com.mikosik.stork.problem.Problem;
import com.mikosik.stork.problem.ProblemException;
import com.mikosik.stork.problem.compile.CannotCompile;
import com.mikosik.stork.problem.compute.CannotCompute;

public class ProgramTest {
  private static final Supplier<Library> CORE = singleton(() -> Core.core(DEVELOPMENT));
  private static final Supplier<Library> MINCORE = singleton(() -> Core.core(TESTING));

  private final Directory root = systemTemporaryDirectory()
      .directory("stork_test_program_" + randomUUID());
  private final FsBuilder fsBuilder = fsBuilder();
  private String name;
  private Library core;

  private byte[] stdin = new byte[0];
  private final Reserver expectedType = reserver();
  private final ExpectedProblems expectedCannotCompile = expectedProblems();
  private final ExpectedProblems expectedCannotCompute = expectedProblems();
  private final ExpectedStdout expectedStdout = expectedStdout();

  private ProgramTest() {}

  public static ProgramTest programTest(String name) {
    return new ProgramTest()
        .name(name)
        .core(CORE);
  }

  public static ProgramTest minimalProgramTest(String name) {
    return new ProgramTest()
        .name(name)
        .core(MINCORE);
  }

  private ProgramTest name(String name) {
    this.name = name;
    return this;
  }

  private ProgramTest core(Supplier<Library> core) {
    this.core = core.get();
    return this;
  }

  public ProgramTest file(String path, String content) {
    fsBuilder.file(path, bytes(content));
    return this;
  }

  public ProgramTest sourceFile(String content) {
    return file(SOURCE_FILE_NAME, content);
  }

  public ProgramTest sourceFile(String directory, String content) {
    return file(in(directory, SOURCE_FILE_NAME), content);
  }

  public ProgramTest importFile(String content) {
    return file(IMPORT_FILE_NAME, content);
  }

  public ProgramTest importFile(String directory, String content) {
    return file(in(directory, IMPORT_FILE_NAME), content);
  }

  private static final String in(String directory, String file) {
    return directory + "/" + file;
  }

  private static final String SOURCE_FILE_NAME = "source.stork";
  private static final String IMPORT_FILE_NAME = "import.stork";

  public ProgramTest stdin(String stdin) {
    this.stdin = bytes(stdin);
    return this;
  }

  public Test stdout(String stdout) {
    expectedType.reserve("stdout");
    expectedStdout.expect(bytes(stdout));
    return newCase(name, () -> tryRun());
  }

  public Test expect(Problem problem) {
    switch (problem) {
      case CannotCompile p -> {
        expectedType.reserve("cannot compile");
        expectedCannotCompile.expect(problem);
      }
      case CannotCompute p -> {
        expectedType.reserve("cannot compute");
        expectedCannotCompute.expect(problem);
      }
      default -> throw runtimeException("unknown problem");
    }
    return newCase(name, () -> tryRun());
  }

  private void tryRun() {
    try {
      fsBuilder
          .directory(root)
          .create();
      run();
    } finally {
      fsBuilder.delete();
    }
  }

  private void run() {
    Library library;
    try {
      library = compile(compilation()
          .source(root)
          .library(core));
    } catch (ProblemException exception) {
      expectedCannotCompile.verify(exception.problems);
      return;
    }
    expectedCannotCompile.verify();

    var program = program(identifier("main"), library);
    var buffer = newBuffer();
    var input = input(stdin);
    var output = buffer.asOutput();
    try {
      program.run(input, output);
    } catch (ProblemException exception) {
      expectedCannotCompute.verify(exception.problems);
      return;
    }
    expectedCannotCompute.verify();

    byte[] actualStdout = buffer.bytes();
    expectedStdout.verify(actualStdout);
  }

  private byte[] bytes(String string) {
    return string.getBytes(UTF_8);
  }
}
