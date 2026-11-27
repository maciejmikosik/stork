package com.mikosik.stork.test.suites.language;

import static com.mikosik.stork.compile.err.Miscompilations.duplicatedFunction;
import static com.mikosik.stork.compile.err.Miscompilations.unboundVariable;
import static com.mikosik.stork.compile.err.Miscompilations.undefinedFunction;
import static com.mikosik.stork.model.exp.Namespace.root;
import static com.mikosik.stork.model.exp.Variable.variable;
import static com.mikosik.stork.test.Factories.reference;
import static com.mikosik.stork.test.ProgramTest.minimalProgramTest;
import static com.mikosik.stork.test.build.StorkDirectoryBuilder.path;
import static org.quackery.Suite.suite;

import org.quackery.Suite;
import org.quackery.Test;

import com.mikosik.stork.test.ProgramTest;

public class TestLinkerMiscompilations {
  public static Test testLinkerMiscompilations() {
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
                .set("variable", variable("var"))))
        .add(programTest("multiple times in same function")
            .source("func { var(var) }")
            .expect(
                unboundVariable()
                    .in(reference("func"))
                    .set("variable", variable("var")),
                unboundVariable()
                    .in(reference("func"))
                    .set("variable", variable("var"))));
  }

  private static Suite reportsUndefinedFunction() {
    return suite("undefined function")
        .add(programTest("once")
            .add(path("a/aa")
                .imports("b/bb/funcB")
                .source("funcA { funcB }"))
            .expect(undefinedFunction()
                .in(reference("a/aa/funcA"))
                .set("function", reference("b/bb/funcB"))))
        .add(programTest("multiple times in same function")
            .add(path("a/aa")
                .imports("b/bb/funcB")
                .source("funcA { funcB(funcB) }"))
            .expect(
                undefinedFunction()
                    .in(reference("a/aa/funcA"))
                    .set("function", reference("b/bb/funcB")),
                undefinedFunction()
                    .in(reference("a/aa/funcA"))
                    .set("function", reference("b/bb/funcB"))));
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
                .set("function", variable("func"))))
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
                    .set("function", variable("funcA")),
                duplicatedFunction()
                    .in(root)
                    .set("function", variable("funcB"))));
  }

  private static ProgramTest programTest(String name) {
    return minimalProgramTest(name);
  }
}
