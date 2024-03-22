package com.mikosik.stork.test.cases;

import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.problem.compute.ExpectedInteger.expectedInteger;
import static com.mikosik.stork.test.ProgramTest.programTest;
import static org.quackery.Suite.suite;

import org.quackery.Test;

import com.mikosik.stork.model.Identifier;

public class TestCompute {
  private static final Identifier mock = identifier("mock");

  public static Test testCompute() {
    return suite("requires integer arguments")
        .add(requiresInteger("equal"))
        .add(requiresInteger("moreThan"))
        .add(negateRequiresInteger())
        .add(requiresInteger("add"))
        .add(requiresInteger("multiply"))
        .add(requiresInteger("divideBy"));
  }

  private static Test requiresInteger(String function) {
    return suite("%s".formatted(function))
        .add(programTest("first argument".formatted(function))
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

  private static Test negateRequiresInteger() {
    return programTest("negate")
        .importFile("lang.integer.negate")
        .sourceFile("""
            mock { mock }
            main(stdin) { negate(mock) }
            """)
        .expect(expectedInteger(mock));
  }
}
