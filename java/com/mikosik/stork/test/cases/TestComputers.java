package com.mikosik.stork.test.cases;

import static com.mikosik.stork.compute.CachingComputer.caching;
import static com.mikosik.stork.compute.Computation.computation;
import static com.mikosik.stork.compute.InstructionComputer.instructionComputer;
import static com.mikosik.stork.compute.ModulingComputer.modulingComputer;
import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.EagerInstruction.eager;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Module.module;
import static com.mikosik.stork.model.Variable.variable;
import static java.util.Arrays.asList;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.quackery.report.AssertException.assertTrue;

import org.quackery.Test;

import com.mikosik.stork.compute.Computation;
import com.mikosik.stork.compute.Computer;
import com.mikosik.stork.model.Instruction;

public class TestComputers {
  public static Test testComputers() {
    return suite("computers")
        .add(suite("can handle empty stack")
            .add(newCase("eager computer", () -> {
              var computer = instructionComputer();
              var computation = computation(eager(x -> identifier("y")));
              var computed = computer.compute(computation);
              assertTrue(computation == computed);
            }))
            .add(newCase("instruction computer", () -> {
              var computer = instructionComputer();
              var computation = computation((Instruction) argument -> argument);
              var computed = computer.compute(computation);
              assertTrue(computation == computed);
            })))
        .add(suite("unclone computation")
            .add(newCase("caching computer", () -> {
              var computer = caching(computation -> computation);
              var computation = computation(variable("variable"));
              assertUncloning(computer, computation);
            }))
            .add(newCase("moduling computer", () -> {
              var identifier = identifier("mock");
              var computer = modulingComputer(module(asList(
                  definition(identifier, identifier))));
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
