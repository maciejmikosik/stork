package com.mikosik.stork.testing;

import static com.mikosik.stork.common.Chain.chain;
import static com.mikosik.stork.common.Chain.chainOf;
import static com.mikosik.stork.data.model.Definition.definition;
import static com.mikosik.stork.data.model.Library.library;
import static com.mikosik.stork.testing.Mock.mock;
import static com.mikosik.stork.tool.Modeler.modelExpression;
import static com.mikosik.stork.tool.Parser.parse;
import static com.mikosik.stork.tool.Printer.print;
import static com.mikosik.stork.tool.Runner.runner;
import static com.mikosik.stork.tool.Runtime.runtime;
import static java.lang.String.format;

import org.quackery.Case;
import org.quackery.report.AssertException;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Library;
import com.mikosik.stork.lib.Libraries;
import com.mikosik.stork.tool.Default;
import com.mikosik.stork.tool.Runner;

public class StorkTest extends Case {
  @SuppressWarnings("hiding")
  private final String name;
  private final Chain<String> givenImportedLibraries;
  private final Chain<String> givenMocks;
  private final Chain<String> givenDefinitions;
  private final String whenExpression;
  private final String thenReturnedExpression;

  private StorkTest(
      String name,
      Chain<String> givenImportedLibraries,
      Chain<String> givenMocks,
      Chain<String> givenDefinitions,
      String whenExpression,
      String thenReturnedExpression) {
    super(replaceEmptyName(name, whenExpression, thenReturnedExpression));
    this.name = name;
    this.givenMocks = givenMocks;
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
        givenMocks,
        givenDefinitions,
        whenExpression,
        thenReturnedExpression);
  }

  public StorkTest givenImported(String library) {
    return new StorkTest(
        name,
        givenImportedLibraries.add(library),
        givenMocks,
        givenDefinitions,
        whenExpression,
        thenReturnedExpression);
  }

  public StorkTest givenMocks(String... mocks) {
    return new StorkTest(
        name,
        givenImportedLibraries,
        chainOf(mocks),
        givenDefinitions,
        whenExpression,
        thenReturnedExpression);
  }

  public StorkTest given(String definition) {
    return new StorkTest(
        name,
        givenImportedLibraries,
        givenMocks,
        givenDefinitions.add(definition),
        whenExpression,
        thenReturnedExpression);
  }

  public StorkTest when(String expression) {
    return new StorkTest(
        name,
        givenImportedLibraries,
        givenMocks,
        givenDefinitions,
        expression,
        thenReturnedExpression);
  }

  public StorkTest thenReturned(String expression) {
    return new StorkTest(
        name,
        givenImportedLibraries,
        givenMocks,
        givenDefinitions,
        whenExpression,
        expression);
  }

  public void run() {
    Chain<Definition> definitions = givenDefinitions
        .map(Default::compileDefinition);
    Chain<Library> libraries = givenImportedLibraries
        .map(Libraries::library);
    Chain<Definition> mocks = givenMocks
        .map(name -> definition(name, mock(name)));
    Chain<Library> allLibraries = libraries
        .add(library(definitions))
        .add(library(mocks));
    Runner runner = runner(runtime(allLibraries));

    Expression actual = runner.run(modelExpression(parse(whenExpression)));
    Expression expected = runner.run(modelExpression(parse(thenReturnedExpression)));

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
          whenExpression,
          thenReturnedExpression,
          print(expected),
          print(actual)));
    }
  }
}
