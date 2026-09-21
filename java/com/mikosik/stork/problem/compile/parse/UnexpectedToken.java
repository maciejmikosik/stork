package com.mikosik.stork.problem.compile.parse;

import static com.mikosik.stork.compile.Problem.problem;

import com.mikosik.stork.compile.Problem;
import com.mikosik.stork.model.token.Token;

public class UnexpectedToken {
  public static Problem unexpected(Token token) {
    return problem("unexpected token")
        .object(token)
        .build();
  }
}
