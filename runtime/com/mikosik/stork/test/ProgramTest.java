package com.mikosik.stork.test;

import static com.mikosik.stork.common.Chain.chain;
import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.common.io.Ascii.ascii;
import static com.mikosik.stork.common.io.Buffer.newBuffer;
import static com.mikosik.stork.common.io.Input.tryInput;
import static com.mikosik.stork.common.io.InputOutput.children;
import static com.mikosik.stork.compile.Bind.join;
import static com.mikosik.stork.compile.Stars.build;
import static com.mikosik.stork.compile.Stars.moduleFromDirectory;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.program.Program.program;
import static com.mikosik.stork.test.Reuse.LANG_AND_PROGRAM_MODULE;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.quackery.Test;
import org.quackery.report.AssertException;

import com.mikosik.stork.common.io.Buffer;
import com.mikosik.stork.common.io.Input;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.program.Program;

public class ProgramTest {
  public static Test testProgramsIn(Path directory) {
    boolean hasFiles = children(directory).anyMatch(Files::isRegularFile);
    boolean hasDirectories = children(directory).anyMatch(Files::isDirectory);
    check(hasFiles != hasDirectories);
    return hasDirectories
        ? buildSuite(directory)
        : buildCase(directory);
  }

  private static Test buildSuite(Path directory) {
    List<Test> suites = children(directory)
        .filter(Files::isDirectory)
        .map(ProgramTest::testProgramsIn)
        .collect(toList());
    return suite(nameOf(directory)).addAll(suites);
  }

  private static Test buildCase(Path directory) {
    return newCase(nameOf(directory), () -> run(directory));
  }

  private static void run(Path directory) {
    Module module = join(chain(
        build(moduleFromDirectory(directory)),
        LANG_AND_PROGRAM_MODULE));
    Program program = program(identifier("main"), module);
    Input stdin = tryInput(directory.resolve("stdin"));
    Buffer buffer = newBuffer();
    program.run(stdin, buffer.asOutput());
    byte[] expectedStdout = tryInput(directory.resolve("stdout")).readAllBytes();
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

  private static String nameOf(Path directory) {
    return unsnake(directory.getFileName().toString());
  }

  private static String unsnake(String string) {
    return string.replace('_', ' ');
  }
}
