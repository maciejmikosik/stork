package com.mikosik.stork.problem.compile.parse;

import com.mikosik.stork.compile.tokenize.Token;

public class UnexpectedToken extends CannotParse {
  public final Token token;

  private UnexpectedToken(Token token) {
    this.token = token;
  }

  public static UnexpectedToken unexpected(Token token) {
    return new UnexpectedToken(token);
  }
}
