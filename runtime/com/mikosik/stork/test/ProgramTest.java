package com.mikosik.stork.test;

import static com.mikosik.stork.common.Chain.chain;
import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.common.io.Ascii.ascii;
import static com.mikosik.stork.common.io.Buffer.newBuffer;
import static com.mikosik.stork.compile.Bind.join;
import static com.mikosik.stork.compile.Stars.build;
import static com.mikosik.stork.compile.Stars.langModule;
import static com.mikosik.stork.compile.Stars.moduleFromDirectory;
import static com.mikosik.stork.compile.Stars.verify;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.program.Program.program;
import static com.mikosik.stork.program.ProgramModule.programModule;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;

import java.util.Arrays;
import java.util.List;

import org.quackery.Test;
import org.quackery.report.AssertException;

import com.mikosik.stork.common.io.Buffer;
import com.mikosik.stork.common.io.Input;
import com.mikosik.stork.common.io.Node;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.program.Program;

public class ProgramTest {
  public static Test testProgramsIn(Node directory) {
    boolean hasFiles = directory.children().anyMatch(Node::isRegularFile);
    boolean hasDirectories = directory.children().anyMatch(Node::isDirectory);
    check(hasFiles != hasDirectories);
    return hasDirectories
        ? buildSuite(directory)
        : buildCase(directory);
  }

  private static Test buildSuite(Node directory) {
    List<Test> suites = directory.children()
        .filter(Node::isDirectory)
        .map(ProgramTest::testProgramsIn)
        .collect(toList());
    return suite(nameOf(directory)).addAll(suites);
  }

  private static Test buildCase(Node directory) {
    return newCase(nameOf(directory), () -> run(directory));
  }

  private static void run(Node directory) {
    List<Module> modules = directory.children()
        .filter(Node::isRegularFile)
        .filter(ProgramTest::isStorkFile)
        .map(file -> moduleFromDirectory(file.parent()))
        .collect(toList());

    Module module = build(verify(join(chain(modules)
        .add(langModule())
        .add(programModule()))));
    Program program = program(identifier("main"), module);
    Input stdin = directory.child("stdin").tryInput();
    Buffer buffer = newBuffer();
    program.run(stdin, buffer.asOutput());
    byte[] expectedStdout = directory.child("stdout").tryInput().readAllBytes();
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

  private static boolean isStorkFile(Node file) {
    return file.name().equals("stork");
  }

  private static String nameOf(Node directory) {
    return unsnake(directory.name());
  }

  private static String unsnake(String string) {
    return string.replace('_', ' ');
  }
}
