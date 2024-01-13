package com.mikosik.stork.test;

import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.common.Sequence.sequence;
import static com.mikosik.stork.common.io.Ascii.ascii;
import static com.mikosik.stork.common.io.Buffer.newBuffer;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.common.io.InputOutput.createTempDirectory;
import static com.mikosik.stork.common.io.Output.output;
import static com.mikosik.stork.compile.Bind.join;
import static com.mikosik.stork.compile.CombinatoryModule.combinatoryModule;
import static com.mikosik.stork.compile.MathModule.mathModule;
import static com.mikosik.stork.compile.Stars.build;
import static com.mikosik.stork.compile.Stars.moduleFromDirectory;
import static com.mikosik.stork.compile.problem.VerifyModule.verify;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.program.Program.program;
import static com.mikosik.stork.program.ProgramModule.programModule;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;

import org.quackery.Body;
import org.quackery.Test;
import org.quackery.report.AssertException;

import com.mikosik.stork.common.io.Buffer;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.program.Program;

public class ProgramTest implements Test {
  private static final Module NATIVE_MODULE = build(verify(join(sequence(
      moduleFromDirectory(Paths.get("core_star")),
      combinatoryModule(),
      mathModule(),
      programModule()))));

  private final String name;
  private final Path directory;
  private final List<File> files = new LinkedList<>();
  private byte[] stdin = new byte[0];
  private byte[] expectedStdout = null;

  private ProgramTest(String name, Path directory) {
    this.name = name;
    this.directory = directory;
  }

  public static ProgramTest programTest(String name) {
    return new ProgramTest(name, createTempDirectory("stork_test_program_"));
  }

  public ProgramTest file(String path, String content) {
    files.add(new File(Paths.get(path), bytes(content)));
    return this;
  }

  public ProgramTest sourceFile(String content) {
    return file("stork", content);
  }

  public ProgramTest stdin(String stdin) {
    this.stdin = bytes(stdin);
    return this;
  }

  public ProgramTest stdout(String expectedStdout) {
    this.expectedStdout = bytes(expectedStdout);
    return this;
  }

  public <R> R visit(BiFunction<String, Body, R> caseHandler,
      BiFunction<String, List<Test>, R> suiteHandler) {
    return caseHandler.apply(name, this::run);
  }

  private void run() {
    check(expectedStdout != null);
    files.forEach(file -> {
      output(directory.resolve(file.path))
          .write(file.content);
    });

    Module module = join(sequence(
        build(moduleFromDirectory(directory)),
        NATIVE_MODULE));
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

  private byte[] bytes(String string) {
    return string.getBytes(UTF_8);
  }

  private static record File(Path path, byte[] content) {}
}
