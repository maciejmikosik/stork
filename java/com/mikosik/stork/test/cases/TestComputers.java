package com.mikosik.stork.test.cases;

import static com.mikosik.stork.compute.CachingComputer.caching;
import static com.mikosik.stork.compute.Computation.computation;
import static com.mikosik.stork.compute.LibraryComputer.computer;
import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Library.libraryOf;
import static com.mikosik.stork.model.Variable.variable;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.quackery.report.AssertException.assertTrue;

import org.quackery.Test;

import com.mikosik.stork.compute.Computation;
import com.mikosik.stork.compute.Computer;

public class TestComputers {
  public static Test testComputers() {
    return suite("computers")
        .add(suite("unclone computation")
            .add(newCase("caching computer", () -> {
              var computer = caching(computation -> computation);
              var computation = computation(variable("variable"));
              assertUncloning(computer, computation);
            }))
            .add(newCase("moduling computer", () -> {
              var identifier = identifier("mock");
              var computer = computer(libraryOf(
                  definition(identifier, identifier)));
              var computation = computation(identifier);
              assertUncloning(computer, computation);
            })));
  }

  private static void assertUncloning(Computer computer, Computation computation) {
    var computed = computer.compute(computation);
    assertTrue(computation == computed);
    computed = computer.compute(computed);
    assertTrue(computation == computed);
    computed = computer.compute(computed);
    assertTrue(computation == computed);
  }
}
