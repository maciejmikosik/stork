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
import static com.mikosik.stork.tool.comp.ExhaustedComputer.exhausted;
import static com.mikosik.stork.tool.comp.ModuleComputer.computer;
import static com.mikosik.stork.tool.comp.SteppingComputer.stepping;
import static com.mikosik.stork.tool.link.DefaultLinker.defaultLinker;
import static com.mikosik.stork.tool.link.NoncollidingLinker.noncolliding;
import static com.mikosik.stork.tool.link.OverridingLinker.overriding;
import static com.mikosik.stork.tool.link.VerbModule.verbModule;
import static java.lang.String.format;

import java.util.List;
import java.util.function.BiFunction;

import org.quackery.Body;
import org.quackery.Test;
import org.quackery.report.AssertException;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.lib.Modules;
import com.mikosik.stork.tool.Default;
import com.mikosik.stork.tool.comp.Computer;
import com.mikosik.stork.tool.link.Linker;

public class StorkTest implements Test {
  private String name;
  private Chain<String> givenImportedModules = empty();
  private Chain<String> givenMocks = empty();
  private Chain<String> givenDefinitions = empty();
  private String whenExpression;
  private String thenReturnedExpression;

  private StorkTest() {}

  public static StorkTest storkTest() {
    return new StorkTest();
  }

  public static StorkTest storkTest(String name) {
    return storkTest().name(name);
  }

  public StorkTest name(String name) {
    StorkTest copy = copy();
    copy.name = name;
    return copy;
  }

  public StorkTest givenImported(String module) {
    StorkTest copy = copy();
    copy.givenImportedModules = add(module, givenImportedModules);
    return copy;
  }

  public StorkTest givenMocks(String... mocks) {
    StorkTest copy = copy();
    copy.givenMocks = chainOf(mocks);
    return copy;
  }

  public StorkTest given(String definition) {
    StorkTest copy = copy();
    copy.givenDefinitions = add(definition, givenDefinitions);
    return copy;
  }

  public StorkTest when(String expression) {
    StorkTest copy = copy();
    copy.whenExpression = expression;
    return copy;
  }

  public StorkTest thenReturned(String expression) {
    StorkTest copy = copy();
    copy.thenReturnedExpression = expression;
    return copy;
  }

  private StorkTest copy() {
    StorkTest copy = new StorkTest();
    copy.name = name;
    copy.givenImportedModules = givenImportedModules;
    copy.givenMocks = givenMocks;
    copy.givenDefinitions = givenDefinitions;
    copy.whenExpression = whenExpression;
    copy.thenReturnedExpression = thenReturnedExpression;
    return copy;
  }

  public <R> R visit(
      BiFunction<String, Body, R> caseHandler,
      BiFunction<String, List<Test>, R> suiteHandler) {
    return caseHandler.apply(name, () -> run());
  }

  private void run() {
    Chain<Definition> definitions = map(Default::compileDefinition, givenDefinitions);
    Chain<Module> modules = map(Modules::module, givenImportedModules);
    Chain<Definition> mocks = map(name -> definition(name, mock(name)), givenMocks);
    Chain<Module> allModules = addAll(
        chainOf(
            module(definitions),
            module(mocks)),
        modules);

    Linker linker = overriding(verbModule(), noncolliding(defaultLinker()));
    Computer computer = exhausted(stepping(computer(linker.link(allModules))));

    Expression actual = computer.compute(compileExpression(whenExpression));
    Expression expected = computer.compute(compileExpression(thenReturnedExpression));

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
