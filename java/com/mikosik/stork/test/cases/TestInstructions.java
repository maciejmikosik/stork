package com.mikosik.stork.test.cases;

import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.problem.compute.ExpectedInteger.expectedInteger;
import static com.mikosik.stork.test.ProgramTest.programTest;
import static org.quackery.Suite.suite;

import org.quackery.Test;

import com.mikosik.stork.model.Identifier;

// TODO rewrite tests for eager operators
public class TestInstructions {
  private static final Identifier mock = identifier("mock");

  public static Test testInstructions() {
    return suite("instructions validate argument types")
        .add(suite("requires integer arguments")
            .add(requiresTwoIntegers("equal"))
            .add(requiresTwoIntegers("moreThan"))
            .add(requiresInteger("negate"))
            .add(requiresTwoIntegers("add"))
            .add(requiresTwoIntegers("multiply"))
            .add(requiresTwoIntegers("divideBy")));
  }

  private static Test requiresTwoIntegers(String function) {
    return suite("%s".formatted(function))
        .add(programTest("first argument")
            .importFile("lang.integer.%s".formatted(function))
            .sourceFile("""
                mock { mock }
                main(stdin) { %s(mock) }
                """.formatted(function))
            .expect(expectedInteger(mock)))
        .add(programTest("second argument")
            .importFile("lang.integer.%s".formatted(function))
            .sourceFile("""
                mock { mock }
                main(stdin) { %s(0)(mock) }
                """.formatted(function))
            .expect(expectedInteger(mock)));
  }

  private static Test requiresInteger(String function) {
    return programTest(function)
        .importFile("lang.integer.%s".formatted(function))
        .sourceFile("""
            mock { mock }
            main(stdin) { %s(mock) }
            """.formatted(function))
        .expect(expectedInteger(mock));
  }
}
