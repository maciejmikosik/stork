package com.mikosik.stork.problem.compile.link;

import static com.mikosik.stork.compile.Problem.problem;

import com.mikosik.stork.compile.Problem;
import com.mikosik.stork.model.exp.Identifier;

public class UndefinedFunction {
  public static Problem undefinedFunction(
      Identifier location,
      Identifier undefined) {
    return problem("undefined function")
        .location(location)
        .object(undefined)
        .build();
  }
}
