package com.mikosik.stork.test;

import static com.mikosik.stork.common.Reserver.reserver;
import static com.mikosik.stork.common.io.Buffer.newBuffer;
import static com.mikosik.stork.common.io.Directory.directory;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.common.io.InputOutput.createTempDirectory;
import static com.mikosik.stork.compile.Compiler.compileDirectory;
import static com.mikosik.stork.compile.link.Modules.join;
import static com.mikosik.stork.compile.link.VerifyModule.verify;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.program.Program.program;
import static com.mikosik.stork.test.CoreLibrary.CORE_LIBRARY;
import static com.mikosik.stork.test.ExpectedProblems.expectedProblems;
import static com.mikosik.stork.test.ExpectedStdout.expectedStdout;
import static com.mikosik.stork.test.FsBuilder.fsBuilder;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.List;
import java.util.function.BiFunction;

import org.quackery.Body;
import org.quackery.Test;

import com.mikosik.stork.common.Reserver;
import com.mikosik.stork.common.io.Directory;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.problem.Problem;
import com.mikosik.stork.problem.ProblemException;
import com.mikosik.stork.problem.compile.CannotCompile;
import com.mikosik.stork.problem.compute.CannotCompute;

public class ProgramTest implements Test {
  private final String name;
  private final FsBuilder fsBuilder;
  private byte[] stdin = new byte[0];

  public final Reserver expectedType = reserver();
  public final ExpectedProblems expectedCannotCompile = expectedProblems();
  public final ExpectedProblems expectedCannotCompute = expectedProblems();
  public final ExpectedStdout expectedStdout = expectedStdout();

  private ProgramTest(String name, Directory directory) {
    this.name = name;
    this.fsBuilder = fsBuilder(directory);
  }

  public static ProgramTest programTest(String name) {
    var directory = directory(createTempDirectory("stork_test_program_"));
    return new ProgramTest(name, directory);
  }

  public ProgramTest file(String path, String content) {
    fsBuilder.file(path, bytes(content));
    return this;
  }

  public ProgramTest sourceFile(String content) {
    file("source", content);
    return this;
  }

  public ProgramTest sourceFile(String directory, String content) {
    file(directory + "/source", content);
    return this;
  }

  public ProgramTest importFile(String content) {
    file("import", content);
    return this;
  }

  public ProgramTest importFile(String directory, String content) {
    file(directory + "/import", content);
    return this;
  }

  public ProgramTest stdin(String stdin) {
    this.stdin = bytes(stdin);
    return this;
  }

  public ProgramTest stdout(String stdout) {
    expectedType.reserve("stdout");
    expectedStdout.expect(bytes(stdout));
    return this;
  }

  public ProgramTest expect(Problem problem) {
    switch (problem) {
      case CannotCompile p -> {
        expectedType.reserve("cannot compile");
        expectedCannotCompile.expect(problem);
      }
      case CannotCompute p -> {
        expectedType.reserve("cannot compute");
        expectedCannotCompute.expect(problem);
      }
      default -> throw new RuntimeException();
    }
    return this;
  }

  public <R> R visit(BiFunction<String, Body, R> caseHandler,
      BiFunction<String, List<Test>, R> suiteHandler) {
    return caseHandler.apply(name, this::tryRun);
  }

  private void tryRun() {
    try {
      run();
    } finally {
      fsBuilder.delete();
    }
  }

  private void run() {
    Module module;
    try {
      module = verify(join(
          compileDirectory(fsBuilder.directory.path),
          CORE_LIBRARY));
    } catch (ProblemException exception) {
      expectedCannotCompile.verify(exception.problems);
      return;
    }
    expectedCannotCompile.verify();

    var program = program(identifier("main"), module);
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
