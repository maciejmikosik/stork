package com.mikosik.stork.test;

import static com.mikosik.stork.Core.Mode.DEVELOPMENT;
import static com.mikosik.stork.Core.Mode.TESTING;
import static com.mikosik.stork.common.Logic.singleton;
import static com.mikosik.stork.common.Throwables.check;
import static com.mikosik.stork.common.io.Buffer.newBuffer;
import static com.mikosik.stork.common.io.Directories.systemTemporaryDirectory;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.compile.Compilation.compilation;
import static com.mikosik.stork.compile.Compiler.compile;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.program.Program.program;
import static com.mikosik.stork.test.FsBuilder.fsBuilder;
import static com.mikosik.stork.test.Outcome.failed;
import static com.mikosik.stork.test.Outcome.printed;
import static com.mikosik.stork.test.QuackeryHelper.assertException;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.deepEquals;
import static java.util.UUID.randomUUID;
import static org.quackery.Case.newCase;

import java.util.function.Supplier;

import org.quackery.Test;

import com.mikosik.stork.Core;
import com.mikosik.stork.compile.Compilation;
import com.mikosik.stork.model.Library;
import com.mikosik.stork.problem.ProblemException;
import com.mikosik.stork.problem.compile.CannotCompile;
import com.mikosik.stork.problem.compute.CannotCompute;

public class ProgramTest {
  private static final Supplier<Library> CORE = singleton(() -> Core.core(DEVELOPMENT));
  private static final Supplier<Library> MINCORE = singleton(() -> Core.core(TESTING));

  private final String name;
  private final FsBuilder fsBuilder;
  private final Compilation compilation;

  private byte[] stdin = new byte[0];
  private Outcome expected;

  private ProgramTest(String name, Library core) {
    this.name = name;
    var root = systemTemporaryDirectory()
        .directory("stork_test_program_" + randomUUID());
    fsBuilder = fsBuilder()
        .directory(root);
    compilation = compilation()
        .library(core)
        .source(root);
  }

  public static ProgramTest programTest(String name) {
    return new ProgramTest(name, CORE.get());
  }

  public static ProgramTest minimalProgramTest(String name) {
    return new ProgramTest(name, MINCORE.get());
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
    expected = printed(bytes(stdout));
    return newCase(name, () -> tryRun());
  }

  public Test expect(CannotCompile problem) {
    expected = failed(problem);
    return newCase(name, () -> tryRun());
  }

  public Test expect(CannotCompute problem) {
    expected = failed(problem);
    return newCase(name, () -> tryRun());
  }

  private void tryRun() {
    try {
      fsBuilder.create();
      run();
    } finally {
      fsBuilder.delete();
    }
  }

  private void run() {
    var actual = compileAndRun();
    if (!deepEquals(expected, actual)) {
      // TODO create common for 2D text manipulation
      throw assertException("expected\n\n%s\n\nactual\n\n%s\n"
          .formatted(expected, actual));
    }
  }

  private Outcome compileAndRun() {
    Library library;
    try {
      library = compile(compilation);
    } catch (ProblemException exception) {
      // TODO throw dedicated internal compiler exception
      check(exception.problem instanceof CannotCompile);
      return failed(exception.problem);
    }

    var program = program(identifier("main"), library);
    var buffer = newBuffer();
    var input = input(stdin);
    var output = buffer.asOutput();
    try {
      program.run(input, output);
    } catch (ProblemException exception) {
      // TODO throw dedicated internal compiler exception
      check(exception.problem instanceof CannotCompute);
      return failed(exception.problem);
    }

    var actualStdout = buffer.bytes();
    return printed(actualStdout);
  }

  private byte[] bytes(String string) {
    return string.getBytes(UTF_8);
  }
}
