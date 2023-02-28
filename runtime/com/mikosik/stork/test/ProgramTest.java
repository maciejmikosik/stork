package com.mikosik.stork.test;

import static com.mikosik.stork.common.Chain.chainFrom;
import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.common.io.Buffer.newBuffer;
import static com.mikosik.stork.common.io.Node.node;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.program.Program.program;
import static com.mikosik.stork.tool.link.Link.link;
import static com.mikosik.stork.tool.link.Stars.moduleFromDirectory;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
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
import com.mikosik.stork.tool.link.Stars;

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
        .map(file -> Stars.moduleFromDirectory(file.parent()))
        .collect(toList());

    Module module = link(chainFrom(modules)
        .add(moduleFromDirectory(node("core_star"))));
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
          new String(expectedStdout, UTF_8),
          new String(actualStdout, UTF_8)));
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
