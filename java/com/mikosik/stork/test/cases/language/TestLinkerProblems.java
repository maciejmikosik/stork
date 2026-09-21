package com.mikosik.stork.test.cases.language;

import static com.mikosik.stork.model.exp.Namespace.namespaceRoot;
import static com.mikosik.stork.model.exp.Variable.variable;
import static com.mikosik.stork.problem.compile.Problems.duplicatedFunction;
import static com.mikosik.stork.problem.compile.Problems.unboundVariable;
import static com.mikosik.stork.problem.compile.Problems.undefinedFunction;
import static com.mikosik.stork.test.Factories.identifier;
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
                .location(identifier("func"))
                .object(variable("var"))))
        .add(programTest("multiple times in same function")
            .source("func { var(var) }")
            .expect(
                unboundVariable()
                    .location(identifier("func"))
                    .object(variable("var")),
                unboundVariable()
                    .location(identifier("func"))
                    .object(variable("var"))));
  }

  private static Suite reportsUndefinedFunction() {
    return suite("undefined function")
        .add(programTest("once")
            .add(path("a/aa")
                .imports("b/bb/funcB")
                .source("funcA { funcB }"))
            .expect(undefinedFunction()
                .location(identifier("a/aa/funcA"))
                .object(identifier("b/bb/funcB"))))
        .add(programTest("multiple times in same function")
            .add(path("a/aa")
                .imports("b/bb/funcB")
                .source("funcA { funcB(funcB) }"))
            .expect(
                undefinedFunction()
                    .location(identifier("a/aa/funcA"))
                    .object(identifier("b/bb/funcB")),
                undefinedFunction()
                    .location(identifier("a/aa/funcA"))
                    .object(identifier("b/bb/funcB"))));
  }

  private static Test reportsDuplicatedFunction() {
    return suite("duplicated function")
        .add(programTest("once")
            .source("""
                func { 1 }
                func { 2 }
                """)
            .expect(duplicatedFunction()
                .location(namespaceRoot())
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
                    .location(namespaceRoot())
                    .object(variable("funcA")),
                duplicatedFunction()
                    .location(namespaceRoot())
                    .object(variable("funcB"))));
  }

  private static ProgramTest programTest(String name) {
    return minimalProgramTest(name);
  }
}
