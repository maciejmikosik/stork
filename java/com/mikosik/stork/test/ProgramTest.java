package com.mikosik.stork.test;

import static com.mikosik.stork.build.Stars.build;
import static com.mikosik.stork.build.Stars.buildCoreLibrary;
import static com.mikosik.stork.build.link.Bind.join;
import static com.mikosik.stork.build.link.problem.VerifyModule.verify;
import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.common.Collections.intersection;
import static com.mikosik.stork.common.Sequence.sequence;
import static com.mikosik.stork.common.io.Ascii.ascii;
import static com.mikosik.stork.common.io.Buffer.newBuffer;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.common.io.InputOutput.createTempDirectory;
import static com.mikosik.stork.common.io.InputOutput.path;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.program.Program.program;
import static com.mikosik.stork.test.FsBuilder.fsBuilder;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toCollection;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

import org.quackery.Body;
import org.quackery.Test;
import org.quackery.report.AssertException;

import com.mikosik.stork.build.BuildException;
import com.mikosik.stork.common.io.Buffer;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Problem;
import com.mikosik.stork.program.Program;

public class ProgramTest implements Test {
  private static final Module NATIVE_MODULE = buildCoreLibrary(path("core_library"));

  private final String name;
  private final FsBuilder fsBuilder;
  private byte[] stdin = new byte[0];
  private byte[] expectedStdout = null;
  private final List<Problem> expectedProblems = new LinkedList<>();

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
    check(expectedProblems.isEmpty());
    this.expectedStdout = bytes(expectedStdout);
    return this;
  }

  public ProgramTest expect(Problem problem) {
    check(expectedStdout == null);
    expectedProblems.add(problem);
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
    if (expectedStdout != null) {
      runAndAssertStdout();
    } else if (!expectedProblems.isEmpty()) {
      buildAndAssertProblems();
    } else {
      throw new RuntimeException();
    }
  }

  private void runAndAssertStdout() {
    Module module = verify(join(sequence(
        build(fsBuilder.directory),
        NATIVE_MODULE)));
    Program program = program(identifier("main"), module);
    Buffer buffer = newBuffer();
    program.run(input(stdin), buffer.asOutput());
    byte[] actualStdout = buffer.bytes();
    if (!Arrays.equals(actualStdout, expectedStdout)) {
      throw new AssertException(format(""
          + "expected output\n"
          + "  %s\n"
          + "but was\n"
          + "  %s\n",
          ascii(expectedStdout),
          ascii(actualStdout)));
    }
  }

  private void buildAndAssertProblems() {
    var actual = descriptions(buildAndReturnProblems());
    var expected = descriptions(expectedProblems);
    var common = intersection(actual, expected);
    actual.removeAll(common);
    expected.removeAll(common);
    if (!expected.isEmpty() || !actual.isEmpty()) {
      throw new AssertException(formatMessage(actual, expected));
    }
  }

  private List<? extends Problem> buildAndReturnProblems() {
    try {
      verify(join(sequence(
          build(fsBuilder.directory),
          NATIVE_MODULE)));
      return emptyList();
    } catch (BuildException exception) {
      return exception.problems;
    }
  }

  private static Set<String> descriptions(List<? extends Problem> problems) {
    return problems.stream()
        .map(Problem::description)
        .collect(toCollection(HashSet::new));
  }

  private static String formatMessage(Set<String> actual, Set<String> expected) {
    var builder = new StringBuilder();
    append(builder, "expected", expected);
    append(builder, "not expected", actual);
    return builder.toString();
  }

  private static void append(
      StringBuilder builder,
      String headline,
      Set<String> problems) {
    if (!problems.isEmpty()) {
      builder.append(headline).append("\n\n");
      for (String problem : problems) {
        builder.append(problem).append("\n");
      }
    }
  }

  private byte[] bytes(String string) {
    return string.getBytes(UTF_8);
  }
}
