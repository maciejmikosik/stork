package com.mikosik.stork.test;

import static com.mikosik.stork.Core.core;
import static com.mikosik.stork.Core.Mode.DEVELOPMENT;
import static com.mikosik.stork.Core.Mode.TESTING;
import static com.mikosik.stork.common.ImmutableList.join;
import static com.mikosik.stork.common.Logic.singleton;
import static com.mikosik.stork.common.io.Buffer.newBuffer;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.common.io.Output.noOutput;
import static com.mikosik.stork.compile.Compiler.compile;
import static com.mikosik.stork.compile.link.VerifyLibrary.verify;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Namespace.namespaceOf;
import static com.mikosik.stork.model.Source.Kind.CODE;
import static com.mikosik.stork.model.Source.Kind.IMPORT;
import static com.mikosik.stork.program.Program.program;
import static com.mikosik.stork.program.Runner.runner;
import static com.mikosik.stork.program.Task.task;
import static com.mikosik.stork.program.Terminal.terminal;
import static com.mikosik.stork.test.Outcome.failed;
import static com.mikosik.stork.test.Outcome.printed;
import static com.mikosik.stork.test.QuackeryHelper.assertException;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.deepEquals;
import static org.quackery.Case.newCase;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import org.quackery.Test;

import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Namespace;
import com.mikosik.stork.model.Source;
import com.mikosik.stork.problem.Problem;
import com.mikosik.stork.problem.compile.CannotCompile;
import com.mikosik.stork.problem.compute.CannotCompute;

public class ProgramTest {
  private static final Supplier<List<Definition>> CORE = singleton(() -> core(DEVELOPMENT));
  private static final Supplier<List<Definition>> MINCORE = singleton(() -> core(TESTING));

  private final String name;
  private final List<Definition> core;
  private final List<Source> sources = new LinkedList<>();
  private Namespace currentNamespace = namespaceOf();
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

  public ProgramTest namespace(String directory) {
    currentNamespace = namespaceOf(directory.split("/"));
    return this;
  }

  public ProgramTest source(Source source) {
    sources.add(source);
    return this;
  }

  public ProgramTest sourceRaw(String content) {
    return source(Source.source(CODE, currentNamespace, bytes(content)));
  }

  public ProgramTest source(String content) {
    return sourceRaw(content.replace('\'', '\"'));
  }

  public ProgramTest imports(String content) {
    return source(Source.source(IMPORT, currentNamespace, bytes(content)));
  }

  public ProgramTest stdin(String stdin) {
    this.stdin = bytes(stdin);
    return this;
  }

  public Test stdout(String stdout) {
    return newCaseExpecting(printed(bytes(stdout)));
  }

  public Test expect(CannotCompile problem) {
    return newCaseExpecting(failed(problem));
  }

  public Test expect(CannotCompute problem) {
    return newCaseExpecting(failed(problem));
  }

  private Test newCaseExpecting(Outcome expected) {
    return newCase(name, () -> compileRunAndAssert(expected));
  }

  private void compileRunAndAssert(Outcome expected) {
    var actual = compileAndRun();
    if (!deepEquals(expected, actual)) {
      // TODO create common for 2D text manipulation
      throw assertException("expected\n\n%s\n\nactual\n\n%s\n"
          .formatted(expected, actual));
    }
  }

  private Outcome compileAndRun() {
    try {
      var library = verify(join(
          compile(sources),
          core));
      var buffer = newBuffer();
      runner().run(task(
          program(identifier("main"), library),
          terminal(input(stdin), buffer.asOutput(), noOutput())));
      return printed(buffer.bytes());
    } catch (Problem problem) {
      return failed(problem);
    }
  }

  private byte[] bytes(String string) {
    return string.getBytes(UTF_8);
  }
}
