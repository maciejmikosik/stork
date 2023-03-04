package com.mikosik.stork.test;

import static com.mikosik.stork.common.Chain.chain;
import static com.mikosik.stork.common.io.Input.input;
import static com.mikosik.stork.common.io.Node.node;
import static com.mikosik.stork.compile.Bind.bindLambdaParameter;
import static com.mikosik.stork.compile.Bind.join;
import static com.mikosik.stork.compile.Bind.linking;
import static com.mikosik.stork.compile.CheckCollisions.checkCollisions;
import static com.mikosik.stork.compile.CheckUndefined.checkUndefined;
import static com.mikosik.stork.compile.CombinatoryModule.combinatoryModule;
import static com.mikosik.stork.compile.Decompiler.decompiler;
import static com.mikosik.stork.compile.MathModule.mathModule;
import static com.mikosik.stork.compile.Stars.moduleFromDirectory;
import static com.mikosik.stork.compile.Unlambda.unlambda;
import static com.mikosik.stork.compile.Unquote.unquote;
import static com.mikosik.stork.compute.ApplicationComputer.applicationComputer;
import static com.mikosik.stork.compute.CachingComputer.caching;
import static com.mikosik.stork.compute.ChainedComputer.chained;
import static com.mikosik.stork.compute.Computation.computation;
import static com.mikosik.stork.compute.Computations.abort;
import static com.mikosik.stork.compute.InstructionComputer.instructionComputer;
import static com.mikosik.stork.compute.InterruptibleComputer.interruptible;
import static com.mikosik.stork.compute.LoopingComputer.looping;
import static com.mikosik.stork.compute.ModulingComputer.modulingComputer;
import static com.mikosik.stork.compute.ReturningComputer.returningComputer;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Linkage.linkage;
import static com.mikosik.stork.model.change.Changes.changeVariable;
import static com.mikosik.stork.model.change.Changes.inExpression;
import static com.mikosik.stork.model.change.Changes.inModule;
import static com.mikosik.stork.program.StdinComputer.stdinComputer;
import static java.lang.String.format;
import static org.quackery.Case.newCase;

import java.util.List;
import java.util.function.BiFunction;

import org.quackery.Body;
import org.quackery.Test;
import org.quackery.report.AssertException;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.compile.Compiler;
import com.mikosik.stork.compute.Computation;
import com.mikosik.stork.compute.Computer;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Linkage;
import com.mikosik.stork.model.Module;

public class SnippetTest implements Test {
  private final String name;
  private Linkage linkage = linkage();
  private Chain<Test> tests = chain();

  private SnippetTest(String name) {
    this.name = name;
  }

  public static SnippetTest snippetTest(String name) {
    return new SnippetTest(name);
  }

  public SnippetTest importing(String global) {
    linkage = linkage.add(identifier(global));
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

    Module module = join(chain(
        mathModule(),
        combinatoryModule(),
        moduleFromDirectory(node("core_star"))));

    checkCollisions(module);
    checkUndefined(module);

    module = inModule(unlambda)
        .andThen(inModule(unquote))
        .apply(module);

    Computer expressing = chained(
        modulingComputer(module),
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
        .andThen(inExpression(changeVariable(linking(linkage))))
        .andThen(inExpression(unlambda))
        .andThen(inExpression(unquote))
        .apply(compiled);
  }

  public <R> R visit(BiFunction<String, Body, R> caseHandler,
      BiFunction<String, List<Test>, R> suiteHandler) {
    return suiteHandler.apply(name, tests.reverse().toLinkedList());
  }
}
