package com.mikosik.stork.test;

import static com.mikosik.stork.common.Chain.chain;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.common.io.Node.node;
import static com.mikosik.stork.model.Computation.computation;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.program.StdinComputer.stdinComputer;
import static com.mikosik.stork.tool.common.CombinatoryModule.combinatoryModule;
import static com.mikosik.stork.tool.common.Computations.abort;
import static com.mikosik.stork.tool.compute.ApplicationComputer.applicationComputer;
import static com.mikosik.stork.tool.compute.CachingComputer.caching;
import static com.mikosik.stork.tool.compute.ChainedComputer.chained;
import static com.mikosik.stork.tool.compute.InstructionComputer.instructionComputer;
import static com.mikosik.stork.tool.compute.InterruptibleComputer.interruptible;
import static com.mikosik.stork.tool.compute.LoopingComputer.looping;
import static com.mikosik.stork.tool.compute.ModulingComputer.modulingComputer;
import static com.mikosik.stork.tool.compute.ReturningComputer.returningComputer;
import static com.mikosik.stork.tool.decompile.Decompiler.decompiler;
import static com.mikosik.stork.tool.link.Bind.bindLambdaParameter;
import static com.mikosik.stork.tool.link.Bind.identifyVariables;
import static com.mikosik.stork.tool.link.Changes.inExpression;
import static com.mikosik.stork.tool.link.Changes.inModule;
import static com.mikosik.stork.tool.link.CheckCollisions.checkCollisions;
import static com.mikosik.stork.tool.link.CheckUndefined.checkUndefined;
import static com.mikosik.stork.tool.link.Link.link;
import static com.mikosik.stork.tool.link.MathModule.mathModule;
import static com.mikosik.stork.tool.link.Stars.moduleFromDirectory;
import static com.mikosik.stork.tool.link.Unlambda.unlambda;
import static com.mikosik.stork.tool.link.Unquote.unquote;
import static java.lang.String.format;
import static org.quackery.Case.newCase;

import java.util.List;
import java.util.function.BiFunction;

import org.quackery.Body;
import org.quackery.Test;
import org.quackery.report.AssertException;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.model.Computation;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.tool.compile.Compiler;
import com.mikosik.stork.tool.compute.Computer;

public class SnippetTest implements Test {
  private final String name;
  private Chain<Identifier> imports = chain();
  private Chain<Test> tests = chain();

  private SnippetTest(String name) {
    this.name = name;
  }

  public static SnippetTest snippetTest(String name) {
    return new SnippetTest(name);
  }

  public SnippetTest importing(String importing) {
    imports = imports.add(identifier(importing));
    return this;
  }

  public SnippetTest test(String actual, String expected) {
    tests = tests.add(newCase(
        format("%s = %s", actual, expected),
        () -> run(actual, expected)));
    return this;
  }

  private final void run(String actual, String expected) {
    String actualComputed = compileAndCompute(actual);
    String expectedComputed = compileAndCompute(expected);
    if (!actualComputed.equals(expectedComputed)) {
      throw new AssertException(format("\n"
          + "  expression\n"
          + "    %s\n"
          + "  computes to\n"
          + "    %s\n"
          + "  but expression\n"
          + "    %s\n"
          + "  computes to\n"
          + "    %s\n",
          actual, actualComputed, expected, expectedComputed));
    }
  }

  private String compileAndCompute(String snippet) {
    Expression compiled = prepareSnippet(snippet);

    Module coreModule = moduleFromDirectory(node("core_star"));
    Module linkedModule = link(chain(
        mathModule(),
        combinatoryModule(),
        coreModule));

    checkCollisions(linkedModule);
    checkUndefined(linkedModule);

    linkedModule = inModule(unlambda)
        .andThen(inModule(unquote))
        .apply(linkedModule);

    Computer expressing = chained(
        modulingComputer(linkedModule),
        instructionComputer(),
        applicationComputer(),
        stdinComputer(),
        returningComputer());
    Computer computer = looping(interruptible(caching(expressing)));

    Computation computed = computer.compute(computation(compiled));

    return decompiler()
        .local()
        .decompile(abort(computed));
  }

  private Expression prepareSnippet(String snippet) {
    Expression compiled = new Compiler().compileExpression(input(snippet));
    return inExpression(bindLambdaParameter)
        .andThen(inExpression(identifyVariables(imports)))
        .andThen(inExpression(unlambda))
        .andThen(inExpression(unquote))
        .apply(compiled);
  }

  public <R> R visit(BiFunction<String, Body, R> caseHandler,
      BiFunction<String, List<Test>, R> suiteHandler) {
    return suiteHandler.apply(name, tests.reverse().toLinkedList());
  }
}
