package com.mikosik.stork;

import static com.mikosik.stork.common.Chain.chain;
import static com.mikosik.stork.common.Chain.chainFrom;
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

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.model.def.Definition;
import com.mikosik.stork.model.runtime.Expression;
import com.mikosik.stork.tool.Compiler;
import com.mikosik.stork.tool.Parser;
import com.mikosik.stork.tool.Runner;

public class Snippet extends Case {
  private final Chain<String> sources;
  private final String launch;
  private final String expect;

  private Snippet(
      String name,
      Chain<String> sources,
      String launch,
      String expect) {
    super(name);
    this.sources = sources;
    this.launch = launch;
    this.expect = expect;
  }

  public static Snippet snippet(String name) {
    return new Snippet(name, chain(), null, null);
  }

  public Snippet define(String definition) {
    return new Snippet(
        name,
        sources.add(definition),
        launch,
        expect);
  }

  public Snippet launch(String launch) {
    return new Snippet(
        name,
        sources,
        launch,
        expect);
  }

  public Snippet expect(String expect) {
    return new Snippet(
        name,
        sources,
        launch,
        expect);
  }

  public void run() {
    List<Definition> definitions = sources.stream()
        .map(Parser::parse)
        .map(Compiler::compileDefinition)
        .collect(toCollection(LinkedList::new));
    Runner runner = runner(runtime(library(chainFrom(definitions))));

    Expression launched = runner.run(compileExpression(parse(launch)));
    Expression expected = runner.run(compileExpression(parse(expect)));

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
