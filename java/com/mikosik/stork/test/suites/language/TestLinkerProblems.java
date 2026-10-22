package com.mikosik.stork.test.suites.language;

import static com.mikosik.stork.compile.err.Problems.duplicatedFunction;
import static com.mikosik.stork.compile.err.Problems.unboundVariable;
import static com.mikosik.stork.compile.err.Problems.undefinedFunction;
import static com.mikosik.stork.model.exp.Namespace.root;
import static com.mikosik.stork.model.exp.Variable.variable;
import static com.mikosik.stork.test.Factories.reference;
import static com.mikosik.stork.test.ProgramTest.minimalProgramTest;
import static com.mikosik.stork.test.StorkDirectoryBuilder.path;
import static org.quackery.Suite.suite;

import org.quackery.Suite;
import org.quackery.Test;

import com.mikosik.stork.test.ProgramTest;

public class TestLinkerProblems {
  public static Test testLinkerProblems() {
    return suite("linker reports")
        .add(reportsUnboundVariable())
        .add(reportsUndefinedFunction())
        .add(reportsDuplicatedFunction());
  }

  private static Test reportsUnboundVariable() {
    return suite("unbound variable")
        .add(programTest("once")
            .source("func { var }")
            .expect(unboundVariable()
                .in(reference("func"))
                .object(variable("var"))))
        .add(programTest("multiple times in same function")
            .source("func { var(var) }")
            .expect(
                unboundVariable()
                    .in(reference("func"))
                    .object(variable("var")),
                unboundVariable()
                    .in(reference("func"))
                    .object(variable("var"))));
  }

  private static Suite reportsUndefinedFunction() {
    return suite("undefined function")
        .add(programTest("once")
            .add(path("a/aa")
                .imports("b/bb/funcB")
                .source("funcA { funcB }"))
            .expect(undefinedFunction()
                .in(reference("a/aa/funcA"))
                .object(reference("b/bb/funcB"))))
        .add(programTest("multiple times in same function")
            .add(path("a/aa")
                .imports("b/bb/funcB")
                .source("funcA { funcB(funcB) }"))
            .expect(
                undefinedFunction()
                    .in(reference("a/aa/funcA"))
                    .object(reference("b/bb/funcB")),
                undefinedFunction()
                    .in(reference("a/aa/funcA"))
                    .object(reference("b/bb/funcB"))));
  }

  private static Test reportsDuplicatedFunction() {
    return suite("duplicated function")
        .add(programTest("once")
            .source("""
                func { 1 }
                func { 2 }
                """)
            .expect(duplicatedFunction()
                .in(root)
                .object(variable("func"))))
        .add(programTest("multiple times in same namespace")
            .source("""
                funcA { 1 }
                funcA { 2 }
                funcB { 1 }
                funcB { 2 }
                """)
            .expect(
                duplicatedFunction()
                    .in(root)
                    .object(variable("funcA")),
                duplicatedFunction()
                    .in(root)
                    .object(variable("funcB"))));
  }

  private static ProgramTest programTest(String name) {
    return minimalProgramTest(name);
  }
}
