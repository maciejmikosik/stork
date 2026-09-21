package com.mikosik.stork.problem.compile.link;

import static com.mikosik.stork.compile.Problem.problem;

import com.mikosik.stork.compile.Problem;
import com.mikosik.stork.model.exp.Identifier;

public class DuplicatedFunction {
  public static Problem duplicatedFunction(Identifier function) {
    return problem("duplicated function")
        .location(function.namespace)
        .object(function.variable)
        .build();
  }
}
