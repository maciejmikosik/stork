package com.mikosik.stork.test;

import static com.mikosik.stork.Core.core;
import static com.mikosik.stork.Core.Mode.DEVELOPMENT;
import static com.mikosik.stork.Core.Mode.TESTING;
import static com.mikosik.stork.common.Logic.singleton;
import static com.mikosik.stork.common.io.Buffer.newBuffer;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.common.io.Output.noOutput;
import static com.mikosik.stork.compile.Compilation.compilation;
import static com.mikosik.stork.compile.Compiler.compile;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Namespace.namespaceOf;
import static com.mikosik.stork.model.StorkFile.ImportFile.importFile;
import static com.mikosik.stork.model.StorkFile.SourceFile.sourceFile;
import static com.mikosik.stork.program.Program.program;
import static com.mikosik.stork.program.Runner.runner;
import static com.mikosik.stork.program.Task.task;
import static com.mikosik.stork.program.Terminal.terminal;
import static com.mikosik.stork.test.MoreReports.format;
import static com.mikosik.stork.test.Outcome.areEqual;
import static com.mikosik.stork.test.Outcome.outcome;
import static com.mikosik.stork.test.QuackeryHelper.assertException;
import static java.lang.String.join;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.quackery.Case.newCase;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import org.quackery.Test;

import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.StorkFile;
import com.mikosik.stork.problem.compile.CannotCompile;
import com.mikosik.stork.problem.compute.CannotCompute;

public class ProgramTest {
  private static final Supplier<List<Definition>> CORE = singleton(() -> core(DEVELOPMENT));
  private static final Supplier<List<Definition>> MINCORE = singleton(() -> core(TESTING));

  private final String name;
  private final List<Definition> core;
  private final List<StorkFile> storkFiles = new LinkedList<>();
  private byte[] stdin = new byte[0];

  private ProgramTest(String name, List<Definition> core) {
    this.name = name;
    this.core = core;
  }

  public static ProgramTest programTest(String name) {
    return new ProgramTest(name, CORE.get());
  }

  public static ProgramTest minimalProgramTest(String name) {
    return new ProgramTest(name, MINCORE.get());
  }

  public ProgramTest add(StorkDirectoryBuilder builder) {
    var storkDirectory = builder.build();
    storkFiles.add(storkDirectory.importFile);
    storkFiles.add(storkDirectory.sourceFile);
    return this;
  }

  public ProgramTest file(StorkFile storkFile) {
    storkFiles.add(storkFile);
    return this;
  }

  public ProgramTest sourceRaw(String content) {
    return file(sourceFile(namespaceOf(), bytes(content)));
  }

  public ProgramTest sourceRaw(byte[] content) {
    return file(sourceFile(namespaceOf(), content));
  }

  public ProgramTest source(String content) {
    return sourceRaw(content.replace('\'', '\"'));
  }

  public ProgramTest imports(String content) {
    return file(importFile(namespaceOf(), bytes(content)));
  }

  public ProgramTest stdin(String stdin) {
    this.stdin = bytes(stdin);
    return this;
  }

  public Test stdout(String stdout) {
    return newCaseExpecting(outcome(bytes(stdout)));
  }

  public Test expect(CannotCompile cannotCompile) {
    return newCaseExpecting(outcome(cannotCompile));
  }

  public Test expect(CannotCompute cannotCompute) {
    return newCaseExpecting(outcome(cannotCompute));
  }

  private Test newCaseExpecting(Outcome expected) {
    return newCase(name, () -> compileRunAndAssert(expected));
  }

  private void compileRunAndAssert(Outcome expected) {
    var actual = compileAndRun();
    if (!areEqual(expected, actual)) {
      throw assertException(join("",
          format("expected", expected),
          format("actual", actual)));
    }
  }

  private Outcome compileAndRun() {
    try {
      var library = compile(compilation()
          .storkFiles(storkFiles)
          .definitions(core));
      var buffer = newBuffer();
      runner().run(task(
          program(identifier("main"), library),
          terminal(input(stdin), buffer.asOutput(), noOutput())));
      return outcome(buffer.bytes());
    } catch (CannotCompile cannotCompile) {
      return outcome(cannotCompile);
    } catch (CannotCompute cannotCompute) {
      return outcome(cannotCompute);
    }
  }

  private byte[] bytes(String string) {
    return string.getBytes(UTF_8);
  }
}
