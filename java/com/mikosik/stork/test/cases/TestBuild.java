package com.mikosik.stork.test.cases;

import static com.mikosik.stork.build.link.problem.DuplicatedDefinition.duplicatedDefinition;
import static com.mikosik.stork.build.link.problem.UndefinedImport.undefinedImport;
import static com.mikosik.stork.build.link.problem.UndefinedVariable.undefinedVariable;
import static com.mikosik.stork.build.link.problem.VerifyModule.verify;
import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.test.CoreLibrary.CORE_LIBRARY;
import static com.mikosik.stork.test.ProgramTest.programTest;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;

import org.quackery.Test;

import com.mikosik.stork.model.Expression;

public class TestBuild {
  private static Expression body = identifier("body");

  public static Test testBuild() {
    return suite("build")
        .add(coreLibraryHasNoProblems())
        .add(suite("linker reports")
            .add(reportsUndefinedVariable())
            .add(reportsUndefinedImport())
            .add(reportsDuplicatedDefinition()));
  }

  private static Test coreLibraryHasNoProblems() {
    return newCase("core library has no problems", () -> {
      verify(CORE_LIBRARY);
    });
  }

  private static Test reportsUndefinedVariable() {
    return programTest("undefined variable")
        .sourceFile("""
            function { variable }
            """)
        .expect(undefinedVariable(
            definition(identifier("function"), body),
            variable("variable")));
  }

  private static Test reportsUndefinedImport() {
    return programTest("undefined import")
        .importFile("""
            namespace.function2
            """)
        .sourceFile("""
            function { function2 }
            """)
        .expect(undefinedImport(
            definition(identifier("function"), body),
            identifier("namespace.function2")));
  }

  private static Test reportsDuplicatedDefinition() {
    return suite("duplicated definition")
        .add(programTest("of user functions")
            .sourceFile("""
                function { 1 }
                function { 2 }
                """)
            .expect(duplicatedDefinition(identifier("function"))))
        .add(programTest("with core function")
            .file("lang/stream/source", """
                length { 1 }
                """)
            .expect(duplicatedDefinition(identifier("lang.stream.length"))));
  }
}
