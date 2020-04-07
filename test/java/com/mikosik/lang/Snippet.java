package com.mikosik.lang;

import static com.mikosik.lang.common.Stream.stream;
import static com.mikosik.lang.model.def.Library.library;
import static com.mikosik.lang.tool.Compiler.compileDefinition;
import static com.mikosik.lang.tool.Compiler.compileExpression;
import static com.mikosik.lang.tool.Parser.parse;
import static com.mikosik.lang.tool.Runner.runner;
import static java.lang.String.format;

import java.util.LinkedList;
import java.util.List;

import org.quackery.Case;
import org.quackery.report.AssertException;

import com.mikosik.lang.model.def.Library;
import com.mikosik.lang.model.runtime.Expression;
import com.mikosik.lang.tool.Runner;

public class Snippet extends Case {
  private final List<String> definitions = new LinkedList<>();
  private String launch;
  private String expect;

  private Snippet(String name) {
    super(name);
  }

  public static Snippet snippet(String name) {
    return new Snippet(name);
  }

  public Snippet define(String definition) {
    definitions.add(definition);
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
    Library library = library();
    for (String definition : definitions) {
      library.add(compileDefinition(parse(stream(definition))));
    }
    Runner runner = runner(library);

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
