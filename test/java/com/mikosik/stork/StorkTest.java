package com.mikosik.stork;

import static com.mikosik.stork.common.Chain.chain;
import static com.mikosik.stork.model.def.Library.library;
import static com.mikosik.stork.tool.Compiler.compileExpression;
import static com.mikosik.stork.tool.Parser.parse;
import static com.mikosik.stork.tool.Runner.runner;
import static com.mikosik.stork.tool.Runtime.runtime;
import static java.lang.String.format;

import org.quackery.Case;
import org.quackery.report.AssertException;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.lib.Libraries;
import com.mikosik.stork.model.def.Definition;
import com.mikosik.stork.model.def.Library;
import com.mikosik.stork.model.runtime.Expression;
import com.mikosik.stork.tool.Compiler;
import com.mikosik.stork.tool.Parser;
import com.mikosik.stork.tool.Runner;

public class StorkTest extends Case {
  @SuppressWarnings("hiding")
  private final String name;
  private final Chain<String> givenImportedLibraries;
  private final Chain<String> givenDefinitions;
  private final String whenExpression;
  private final String thenReturnedExpression;

  private StorkTest(
      String name,
      Chain<String> givenImportedLibraries,
      Chain<String> givenDefinitions,
      String whenExpression,
      String thenReturnedExpression) {
    super(replaceEmptyName(name, whenExpression, thenReturnedExpression));
    this.name = name;
    this.givenDefinitions = givenDefinitions;
    this.givenImportedLibraries = givenImportedLibraries;
    this.whenExpression = whenExpression;
    this.thenReturnedExpression = thenReturnedExpression;
  }

  private static String replaceEmptyName(String name, String when, String then) {
    return name.isEmpty()
        ? format("%s = %s", when, then)
        : name;
  }

  public static StorkTest storkTest() {
    return new StorkTest(
        "",
        chain(),
        chain(),
        null,
        null);
  }

  public static StorkTest storkTest(String name) {
    return storkTest().name(name);
  }

  public StorkTest name(String name) {
    return new StorkTest(
        name,
        givenImportedLibraries,
        givenDefinitions,
        whenExpression,
        thenReturnedExpression);
  }

  public StorkTest givenImported(String library) {
    return new StorkTest(
        name,
        givenImportedLibraries.add(library),
        givenDefinitions,
        whenExpression,
        thenReturnedExpression);
  }

  public StorkTest given(String definition) {
    return new StorkTest(
        name,
        givenImportedLibraries,
        givenDefinitions.add(definition),
        whenExpression,
        thenReturnedExpression);
  }

  public StorkTest when(String expression) {
    return new StorkTest(
        name,
        givenImportedLibraries,
        givenDefinitions,
        expression,
        thenReturnedExpression);
  }

  public StorkTest thenReturned(String expression) {
    return new StorkTest(
        name,
        givenImportedLibraries,
        givenDefinitions,
        whenExpression,
        expression);
  }

  public void run() {
    Chain<Definition> definitions = givenDefinitions
        .map(Parser::parse)
        .map(Compiler::compileDefinition);
    Chain<Library> libraries = givenImportedLibraries
        .map(Libraries::library);
    Chain<Library> allLibraries = libraries.add(library(definitions));
    Runner runner = runner(runtime(allLibraries));

    Expression actual = runner.run(compileExpression(parse(whenExpression)));
    Expression expected = runner.run(compileExpression(parse(thenReturnedExpression)));

    if (!expected.toString().equals(actual.toString())) {
      throw new AssertException(format(""
          + "expected that expression\n"
          + "  %s\n"
          + "returns\n"
          + "  %s\n"
          + "which evaluates to\n"
          + "  %s\n"
          + "but returned\n"
          + "  %s\n",
          whenExpression, thenReturnedExpression, expected, actual));
    }
  }
}
