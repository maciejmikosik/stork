package com.mikosik.stork.testing;

import static com.mikosik.stork.common.Chain.add;
import static com.mikosik.stork.common.Chain.empty;
import static com.mikosik.stork.common.Chains.addAll;
import static com.mikosik.stork.common.Chains.chainOf;
import static com.mikosik.stork.common.Chains.map;
import static com.mikosik.stork.data.model.Definition.definition;
import static com.mikosik.stork.data.model.Module.module;
import static com.mikosik.stork.testing.Mock.mock;
import static com.mikosik.stork.tool.Default.compileExpression;
import static com.mikosik.stork.tool.Printer.print;
import static com.mikosik.stork.tool.link.DefaultLinker.defaultLinker;
import static com.mikosik.stork.tool.link.NoncollidingLinker.noncolliding;
import static com.mikosik.stork.tool.link.OverridingLinker.overriding;
import static com.mikosik.stork.tool.link.VerbModule.verbModule;
import static com.mikosik.stork.tool.run.ExhaustedRunner.exhausted;
import static com.mikosik.stork.tool.run.ModuleRunner.runner;
import static com.mikosik.stork.tool.run.Stepper.stepper;
import static java.lang.String.format;

import org.quackery.Case;
import org.quackery.report.AssertException;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.lib.Modules;
import com.mikosik.stork.tool.Default;
import com.mikosik.stork.tool.link.Linker;
import com.mikosik.stork.tool.run.Runner;

public class StorkTest extends Case {
  @SuppressWarnings("hiding")
  private final String name;
  private final Chain<String> givenImportedModules;
  private final Chain<String> givenMocks;
  private final Chain<String> givenDefinitions;
  private final String whenExpression;
  private final String thenReturnedExpression;

  private StorkTest(
      String name,
      Chain<String> givenImportedModules,
      Chain<String> givenMocks,
      Chain<String> givenDefinitions,
      String whenExpression,
      String thenReturnedExpression) {
    super(replaceEmptyName(name, whenExpression, thenReturnedExpression));
    this.name = name;
    this.givenMocks = givenMocks;
    this.givenDefinitions = givenDefinitions;
    this.givenImportedModules = givenImportedModules;
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
        empty(),
        empty(),
        empty(),
        null,
        null);
  }

  public static StorkTest storkTest(String name) {
    return storkTest().name(name);
  }

  public StorkTest name(String name) {
    return new StorkTest(
        name,
        givenImportedModules,
        givenMocks,
        givenDefinitions,
        whenExpression,
        thenReturnedExpression);
  }

  public StorkTest givenImported(String module) {
    return new StorkTest(
        name,
        add(module, givenImportedModules),
        givenMocks,
        givenDefinitions,
        whenExpression,
        thenReturnedExpression);
  }

  public StorkTest givenMocks(String... mocks) {
    return new StorkTest(
        name,
        givenImportedModules,
        chainOf(mocks),
        givenDefinitions,
        whenExpression,
        thenReturnedExpression);
  }

  public StorkTest given(String definition) {
    return new StorkTest(
        name,
        givenImportedModules,
        givenMocks,
        add(definition, givenDefinitions),
        whenExpression,
        thenReturnedExpression);
  }

  public StorkTest when(String expression) {
    return new StorkTest(
        name,
        givenImportedModules,
        givenMocks,
        givenDefinitions,
        expression,
        thenReturnedExpression);
  }

  public StorkTest thenReturned(String expression) {
    return new StorkTest(
        name,
        givenImportedModules,
        givenMocks,
        givenDefinitions,
        whenExpression,
        expression);
  }

  public void run() {
    Chain<Definition> definitions = map(Default::compileDefinition, givenDefinitions);
    Chain<Module> modules = map(Modules::module, givenImportedModules);
    Chain<Definition> mocks = map(name -> definition(name, mock(name)), givenMocks);
    Chain<Module> allModules = addAll(
        chainOf(
            module(definitions),
            module(mocks)),
        modules);
    Linker linker = overriding(verbModule(), noncolliding(defaultLinker()));
    Runner runner = exhausted(stepper(runner(linker.link(allModules))));

    Expression actual = runner.run(compileExpression(whenExpression));
    Expression expected = runner.run(compileExpression(thenReturnedExpression));

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
