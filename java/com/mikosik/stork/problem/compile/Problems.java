package com.mikosik.stork.problem.compile;

import static com.mikosik.stork.compile.Problem.problem;

import com.mikosik.stork.compile.Problem.ProblemBuilder;

public class Problems {
  public static ProblemBuilder malformedImportLine() {
    return problem("malformed import");
  }

  public static ProblemBuilder unexpectedToken() {
    return problem("unexpected token");
  }

  public static ProblemBuilder illegalCharacterInCode() {
    return problem("illegal character in code");
  }

  public static ProblemBuilder illegalCharacterInString() {
    return problem("unexpected token");
  }

  public static ProblemBuilder unboundVariable() {
    return problem("unbound variable");
  }

  public static ProblemBuilder undefinedFunction() {
    return problem("undefined function");
  }

  public static ProblemBuilder duplicatedFunction() {
    return problem("duplicated function");
  }
}
