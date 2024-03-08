package com.mikosik.stork.test;

import static com.mikosik.stork.build.Stars.build;
import static com.mikosik.stork.build.link.Modules.join;
import static com.mikosik.stork.build.link.problem.VerifyModule.verify;
import static com.mikosik.stork.common.io.Buffer.newBuffer;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.common.io.InputOutput.createTempDirectory;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.program.Program.program;
import static com.mikosik.stork.test.CoreLibrary.CORE_LIBRARY;
import static com.mikosik.stork.test.Expectations.expectations;
import static com.mikosik.stork.test.FsBuilder.fsBuilder;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.nio.file.Path;
import java.util.List;
import java.util.function.BiFunction;

import org.quackery.Body;
import org.quackery.Test;

import com.mikosik.stork.build.ProblemException;
import com.mikosik.stork.common.io.Buffer;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Problem;
import com.mikosik.stork.program.Program;

public class ProgramTest implements Test {
  private final String name;
  private final FsBuilder fsBuilder;
  private byte[] stdin = new byte[0];
  private final Expectations expectations = expectations();

  private ProgramTest(String name, Path directory) {
    this.name = name;
    this.fsBuilder = fsBuilder(directory);
  }

  public static ProgramTest programTest(String name) {
    return new ProgramTest(name, createTempDirectory("stork_test_program_"));
  }

  public ProgramTest file(String path, String content) {
    fsBuilder.file(path, content);
    return this;
  }

  public ProgramTest sourceFile(String content) {
    fsBuilder.sourceFile(content);
    return this;
  }

  public ProgramTest importFile(String content) {
    fsBuilder.importFile(content);
    return this;
  }

  public ProgramTest stdin(String stdin) {
    this.stdin = bytes(stdin);
    return this;
  }

  public ProgramTest stdout(String expectedStdout) {
    expectations.stdout(bytes(expectedStdout));
    return this;
  }

  public ProgramTest expect(Problem problem) {
    expectations.expect(problem);
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
          build(fsBuilder.directory),
          CORE_LIBRARY));
    } catch (ProblemException exception) {
      expectations.actualProblems(exception.problems);
      return;
    }
    expectations.actualProblems();

    Program program = program(identifier("main"), module);
    Buffer buffer = newBuffer();
    program.run(input(stdin), buffer.asOutput());
    byte[] actualStdout = buffer.bytes();
    expectations.actualStdout(actualStdout);
  }

  private byte[] bytes(String string) {
    return string.getBytes(UTF_8);
  }
}
