package com.mikosik.stork.test.cases.language;

import static com.mikosik.stork.common.ImmutableList.single;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Namespace.namespace;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.problem.compile.link.DuplicatedFunction.duplicatedFunction;
import static com.mikosik.stork.problem.compile.link.FunctionNotDefined.functionNotDefined;
import static com.mikosik.stork.problem.compile.link.VariableCannotBeBound.variableCannotBeBound;
import static com.mikosik.stork.test.ProgramTest.minimalProgramTest;
import static org.quackery.Suite.suite;

import org.quackery.Test;

import com.mikosik.stork.test.ProgramTest;

public class TestLinkerProblems {
  public static Test testLinkerProblems() {
    return suite("linker reports")
        .add(reportsVariableThatCannotBeBound())
        .add(reportsFunctionNotDefined())
        .add(reportsDuplicatedFunction());
  }

  private static Test reportsVariableThatCannotBeBound() {
    return programTest("variable that cannot be bound")
        .source("function { variable }")
        .expect(variableCannotBeBound(
            identifier(variable("function")),
            variable("variable")));
  }

  private static Test reportsFunctionNotDefined() {
    return programTest("function that is not defined")
        .imports("namespace/function2")
        .source("function { function2 }")
        .expect(functionNotDefined(
            identifier(variable("function")),
            identifier(namespace(single("namespace")), variable("function2"))));
  }

  private static Test reportsDuplicatedFunction() {
    return programTest("duplicated function")
        .source("""
            function { 1 }
            function { 2 }
            """)
        .expect(duplicatedFunction(
            identifier(variable("function"))));
  }

  private static ProgramTest programTest(String name) {
    return minimalProgramTest(name);
  }
}
