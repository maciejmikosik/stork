package com.mikosik.stork.problem.compile.parse;

import static com.mikosik.stork.common.Description.description;

import com.mikosik.stork.common.Description;
import com.mikosik.stork.compile.tokenize.Bracket;
import com.mikosik.stork.compile.tokenize.IntegerLiteral;
import com.mikosik.stork.compile.tokenize.Label;
import com.mikosik.stork.compile.tokenize.StringLiteral;
import com.mikosik.stork.compile.tokenize.Symbol;
import com.mikosik.stork.compile.tokenize.Token;

public class UnexpectedToken extends CannotParse {
  private final Token token;

  private UnexpectedToken(Token token) {
    this.token = token;
  }

  public static UnexpectedToken unexpected(Token token) {
    return new UnexpectedToken(token);
  }

  public Description describe() {
    return description(textOfDescription());
  }

  private String textOfDescription() {
    return switch (token) {
      case Label label -> "unexpected label [%s]"
          .formatted(label.string);
      case Bracket bracket -> "unexpected bracket [%c]"
          .formatted(bracket.character);
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
