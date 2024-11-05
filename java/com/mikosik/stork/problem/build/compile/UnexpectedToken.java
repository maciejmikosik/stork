package com.mikosik.stork.problem.build.compile;

import com.mikosik.stork.build.compile.IntegerLiteral;
import com.mikosik.stork.build.compile.Label;
import com.mikosik.stork.build.compile.StringLiteral;
import com.mikosik.stork.build.compile.Symbol;
import com.mikosik.stork.build.compile.Token;

public class UnexpectedToken implements CannotCompile {
  private final Token token;

  private UnexpectedToken(Token token) {
    this.token = token;
  }

  public static UnexpectedToken unexpected(Token token) {
    return new UnexpectedToken(token);
  }

  public String description() {
    return switch (token) {
      case Label label -> "unexpected label [%s]"
          .formatted(label.string);
      case Symbol symbol -> "unexpected symbol [%c]"
          .formatted(symbol.character);
      case IntegerLiteral literal -> "unexpected integer literal [%s]"
          .formatted(literal.value);
      case StringLiteral literal -> "unexpected string literal [%s]"
          .formatted(literal.string);
      default -> "unexpected token [%s]"
          .formatted(token.getClass().getSimpleName());
    };
  }
}
