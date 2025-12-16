package com.mikosik.stork.test.cases.language;

import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.problem.compile.link.FunctionDefinedMoreThanOnce.functionDefinedMoreThanOnce;
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
        .add(reportsFunctionDefinedMoreThanOnce());
  }

  private static Test reportsVariableThatCannotBeBound() {
    return programTest("variable that cannot be bound")
        .sourceFile("""
            function { variable }
            """)
        .expect(variableCannotBeBound(
            identifier("function"),
            variable("variable")));
  }

  private static Test reportsFunctionNotDefined() {
    return programTest("function that is not defined")
        .importFile("""
            namespace.function2
            """)
        .sourceFile("""
            function { function2 }
            """)
        .expect(functionNotDefined(
            identifier("function"),
            identifier("namespace.function2")));
  }

  private static Test reportsFunctionDefinedMoreThanOnce() {
    return programTest("function defined more than once")
        .sourceFile("""
            function { 1 }
            function { 2 }
            """)
        .expect(functionDefinedMoreThanOnce(identifier("function")));
  }

  private static ProgramTest programTest(String name) {
    return minimalProgramTest(name);
  }
}
