package com.mikosik.stork.testing;

import static com.mikosik.stork.common.Chains.chainOf;
import static com.mikosik.stork.common.Chains.map;
import static com.mikosik.stork.common.InputOutput.readAllBytes;
import static com.mikosik.stork.main.Program.program;
import static com.mikosik.stork.tool.Default.compileExpression;
import static com.mikosik.stork.tool.link.DefaultLinker.defaultLinker;
import static com.mikosik.stork.tool.link.NoncollidingLinker.noncolliding;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.deepEquals;
import static org.quackery.Case.newCase;

import org.quackery.Test;
import org.quackery.report.AssertException;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.lib.Modules;
import com.mikosik.stork.main.Program;
import com.mikosik.stork.tool.link.Linker;

public class ProgramTest {
  public static Test programTest(String mainUsingSingleQuotes, String expectedOutput) {
    String main = mainUsingSingleQuotes.replace('\'', '\"');
    String name = format("%s : %s", main, expectedOutput);
    return newCase(name, () -> run(main, expectedOutput));
  }

  private static void run(String main, String expectedOutput) {
    Chain<Module> modules = map(Modules::module, chainOf(
        "boolean.stork",
        "integer.stork",
        "stream.stork",
        "optional.stork",
        "function.stork",
        "program.stork"));

    Chain<Module> allModules = modules;

    Linker linker = noncolliding(defaultLinker());
    Module linkedModule = linker.link(allModules);

    Program program = program(compileExpression(main), linkedModule);
    byte[] allBytes = readAllBytes(program.run());
    String output = new String(allBytes, UTF_8);

    if (!deepEquals(output, expectedOutput)) {
      throw new AssertException(format(""
          + "expected that program with input\n"
          + "  %s\n"
          + "returns output\n"
          + "  %s\n"
          + "but returned\n"
          + "  %s\n",
          main,
          expectedOutput,
          output));
    }
  }
}
