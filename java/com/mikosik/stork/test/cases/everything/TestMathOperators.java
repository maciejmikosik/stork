package com.mikosik.stork.test.cases.everything;

import static com.mikosik.stork.problem.compute.CannotCompute.cannotCompute;
import static com.mikosik.stork.test.ProgramTest.programTest;
import static org.quackery.Suite.suite;

import org.quackery.Test;

/**
 * TODO Improve tests. Those tests fail not because specific integer function
 * requires integer arguments. They fail because argument "" computes to
 * expression of combinator without enough arguments. At least we are testing
 * that they are EAGER and redirect computing to arguments.
 */
public class TestMathOperators {
  public static Test testMathOperators() {
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
    return suite(function)
        .add(programTest("first argument")
            .importFile("lang.integer.%s".formatted(function))
            .sourceFile("""
                main(stdin) { %s("") }
                """.formatted(function))
            .expect(cannotCompute()))
        .add(programTest("second argument")
            .importFile("lang.integer.%s".formatted(function))
            .sourceFile("""
                main(stdin) { %s(0)("") }
                """.formatted(function))
            .expect(cannotCompute()));
  }

  private static Test requiresInteger(String function) {
    return programTest(function)
        .importFile("lang.integer.%s".formatted(function))
        .sourceFile("""
            main(stdin) { %s("") }
            """.formatted(function))
        .expect(cannotCompute());
  }
}
