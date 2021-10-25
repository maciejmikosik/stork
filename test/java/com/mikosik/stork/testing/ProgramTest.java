package com.mikosik.stork.testing;

import static com.mikosik.stork.common.Chain.chainFrom;
import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.common.Input.tryInput;
import static com.mikosik.stork.common.InputOutput.list;
import static com.mikosik.stork.common.Output.output;
import static com.mikosik.stork.front.program.Program.program;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.tool.link.Link.link;
import static com.mikosik.stork.tool.link.Stars.moduleFromDirectory;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.quackery.Test;
import org.quackery.report.AssertException;

import com.mikosik.stork.common.Input;
import com.mikosik.stork.front.program.Program;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.tool.link.Stars;

public class ProgramTest {
  public static Test testProgramsIn(Path directory) {
    boolean hasFiles = list(directory).anyMatch(Files::isRegularFile);
    boolean hasDirectories = list(directory).anyMatch(Files::isDirectory);
    check(hasFiles != hasDirectories);
    return hasDirectories
        ? buildSuite(directory)
        : buildCase(directory);
  }

  private static Test buildSuite(Path directory) {
    List<Test> suites = list(directory)
        .filter(Files::isDirectory)
        .map(ProgramTest::testProgramsIn)
        .collect(toList());
    return suite(nameOf(directory)).addAll(suites);
  }

  private static Test buildCase(Path directory) {
    return newCase(nameOf(directory), () -> run(directory));
  }

  private static void run(Path directory) {
    List<Module> modules = list(directory)
        .filter(Files::isRegularFile)
        .filter(ProgramTest::isStorkFile)
        .map(file -> Stars.moduleFromDirectory(file.getParent()))
        .collect(toList());

    Module module = link(chainFrom(modules)
        .add(moduleFromDirectory(Paths.get("core_star"))));
    Program program = program(variable("main"), module);
    Input stdin = tryInput(directory.resolve("stdin"));
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    program.run(stdin, output(buffer));
    byte[] expectedStdout = tryInput(directory.resolve("stdout")).readAllBytes();
    byte[] actualStdout = buffer.toByteArray();
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

  private static boolean isStorkFile(Path file) {
    return file.getFileName().toString().equals("stork");
  }

  private static String nameOf(Path directory) {
    return unsnake(directory.getFileName().toString());
  }

  private static String unsnake(String string) {
    return string.replace('_', ' ');
  }
}
