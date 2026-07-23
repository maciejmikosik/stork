package com.mikosik.stork.test.cases.unit;

import static com.mikosik.stork.common.Description.description;
import static com.mikosik.stork.common.ImmutableList.list;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Namespace.namespace;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.problem.Describer.describe;
import static com.mikosik.stork.problem.compute.CannotCompute.cannotCompute;
import static com.mikosik.stork.problem.compute.FunctionMissing.functionMissing;
import static com.mikosik.stork.test.Assertions.assertMatch;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;

import org.quackery.Test;

import com.mikosik.stork.common.Description;
import com.mikosik.stork.problem.compute.CannotCompute;

public class TestDescriberOfComputerProblems {
  public static Test testDescriberOfComputerProblems() {
    return suite("describer can describe computer problems")
        .add(test(
            cannotCompute(),
            "cannot compute"))
        .add(test(
            functionMissing(
                identifier(namespace(list("a", "b")), variable("c"))),
            "function [a/b/c] is missing"));
  }

  private static Test test(CannotCompute cannotCompute, Description expected) {
    return newCase(cannotCompute.getClass().getSimpleName(), () -> {
      assertMatch(expected, describe(cannotCompute));
    });
  }

  private static Test test(CannotCompute cannotCompute, String expected) {
    return test(cannotCompute, description(expected));
  }
}
