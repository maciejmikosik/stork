package com.mikosik.stork.testing;

import static com.mikosik.stork.common.Ascii.DOUBLE_QUOTE;
import static com.mikosik.stork.common.Ascii.SINGLE_QUOTE;
import static com.mikosik.stork.common.Chain.chainOf;
import static com.mikosik.stork.common.InputOutput.readAllBytes;
import static com.mikosik.stork.data.model.Variable.variable;
import static com.mikosik.stork.main.Program.program;
import static com.mikosik.stork.tool.compile.Modeler.modelModule;
import static com.mikosik.stork.tool.compile.Parser.parse;
import static com.mikosik.stork.tool.link.Linker.coreModule;
import static com.mikosik.stork.tool.link.Linker.link;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.deepEquals;
import static org.quackery.Case.newCase;

import org.quackery.Test;
import org.quackery.report.AssertException;

import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.main.Program;

public class ProgramTest {
  public static Test programTest(
      String name,
      String mainModuleCodeSingleQuoted,
      String expectedOutput) {
    String mainModuleCode = mainModuleCodeSingleQuoted
        .replace(SINGLE_QUOTE, DOUBLE_QUOTE);
    return newCase(name, () -> run(mainModuleCode, expectedOutput));
  }

  private static void run(String mainModuleCode, String expectedOutput) {
    Module module = link(chainOf(
        coreModule(),
        modelModule(parse(mainModuleCode))));
    Program program = program(variable("main"), module);
    byte[] allBytes = readAllBytes(program.run());
    String output = new String(allBytes, UTF_8);

    if (!deepEquals(output, expectedOutput)) {
      throw new AssertException(format(""
          + "expected output\n"
          + "  %s\n"
          + "but was\n"
          + "  %s\n",
          expectedOutput,
          output));
    }
  }
}
