package com.mikosik.stork.testing;

import static com.mikosik.stork.common.Chain.add;
import static com.mikosik.stork.common.Chains.chainOf;
import static com.mikosik.stork.common.Chains.map;
import static com.mikosik.stork.common.InputOutput.readAllBytes;
import static com.mikosik.stork.data.model.Definition.definition;
import static com.mikosik.stork.data.model.Module.module;
import static com.mikosik.stork.data.model.Variable.variable;
import static com.mikosik.stork.main.Program.program;
import static com.mikosik.stork.tool.Default.compileExpression;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.deepEquals;
import static org.quackery.Case.newCase;

import org.quackery.Test;
import org.quackery.report.AssertException;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.lib.Modules;
import com.mikosik.stork.main.Program;

public class ProgramTest {
  public static Test programTest(String mainBodyUsingSingleQuotes, String expectedOutput) {
    String mainBody = mainBodyUsingSingleQuotes.replace('\'', '\"');
    String name = format("%s : %s", mainBody, expectedOutput);
    return newCase(name, () -> run(mainBody, expectedOutput));
  }

  private static void run(String mainFunctionBody, String expectedOutput) {
    Chain<Module> modules = map(Modules::module, chainOf(
        "boolean.stork",
        "integer.stork",
        "stream.stork",
        "optional.stork",
        "function.stork",
        "program.stork"));

    String mainFunctionName = "main";
    Definition mainFunction = definition(
        variable(mainFunctionName),
        compileExpression(mainFunctionBody));
    Chain<Module> allModules = add(module(chainOf(mainFunction)), modules);

    Program program = program(mainFunctionName, allModules);
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
          mainFunctionBody,
          expectedOutput,
          output));
    }
  }
}
