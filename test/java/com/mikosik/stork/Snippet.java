package com.mikosik.stork;

import static com.mikosik.stork.common.Stream.stream;
import static com.mikosik.stork.model.def.Library.library;
import static com.mikosik.stork.tool.Compiler.compileExpression;
import static com.mikosik.stork.tool.Parser.parse;
import static com.mikosik.stork.tool.Runner.runner;
import static com.mikosik.stork.tool.Runtime.runtime;
import static java.lang.String.format;
import static java.util.stream.Collectors.toCollection;

import java.util.LinkedList;
import java.util.List;

import org.quackery.Case;
import org.quackery.report.AssertException;

import com.mikosik.stork.common.Stream;
import com.mikosik.stork.model.def.Definition;
import com.mikosik.stork.model.runtime.Expression;
import com.mikosik.stork.tool.Compiler;
import com.mikosik.stork.tool.Parser;
import com.mikosik.stork.tool.Runner;

public class Snippet extends Case {
  private final List<String> sources = new LinkedList<>();
  private String launch;
  private String expect;

  private Snippet(String name) {
    super(name);
  }

  public static Snippet snippet(String name) {
    return new Snippet(name);
  }

  public Snippet define(String definition) {
    sources.add(definition);
    return this;
  }

  public Snippet launch(String launch) {
    this.launch = launch;
    return this;
  }

  public Snippet expect(String expect) {
    this.expect = expect;
    return this;
  }

  public void run() throws Throwable {
    List<Definition> definitions = sources.stream()
        .map(Stream::stream)
        .map(Parser::parse)
        .map(Compiler::compileDefinition)
        .collect(toCollection(LinkedList::new));
    Runner runner = runner(runtime(library(definitions)));

    Expression launched = runner.run(compileExpression(parse(stream(launch))));
    Expression expected = runner.run(compileExpression(parse(stream(expect))));

    if (!expected.toString().equals(launched.toString())) {
      throw new AssertException(format(""
          + "expected\n"
          + "  %s\n"
          + "but returned\n"
          + "  %s\n",
          expected, launched));
    }
  }
}
