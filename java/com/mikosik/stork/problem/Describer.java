package com.mikosik.stork.problem;

import static com.mikosik.stork.common.ImmutableList.join;
import static com.mikosik.stork.common.ImmutableList.single;
import static com.mikosik.stork.common.Reflection.read;
import static com.mikosik.stork.common.io.Ascii.isAscii;
import static com.mikosik.stork.common.io.Ascii.isPrintable;
import static com.mikosik.stork.common.text.Outline.outline;
import static java.lang.Byte.toUnsignedInt;
import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.Arrays.stream;

import com.mikosik.stork.common.text.Outline;
import com.mikosik.stork.model.exp.Identifier;
import com.mikosik.stork.model.exp.Variable;
import com.mikosik.stork.model.token.Bracket;
import com.mikosik.stork.model.token.IntegerLiteral;
import com.mikosik.stork.model.token.Label;
import com.mikosik.stork.model.token.StringLiteral;
import com.mikosik.stork.model.token.Symbol;
import com.mikosik.stork.model.token.Token;

public class Describer {
  public static Outline describe(Object problem) {
    return outline(problem.getClass().getSimpleName())
        .nest(stream(problem.getClass().getFields())
            .map(field -> format(
                "%s: %s",
                field.getName(),
                formatArgument(read(field, problem))))
            .map(Outline::outline)
            .toList());
  }

  private static String formatArgument(Object arg) {
    return switch (arg) {
      case Token token -> switch (token) {
        case Label label -> label.string;
        case Bracket bracket -> "" + (char) bracket.character;
        case Symbol symbol -> "" + (char) symbol.character;
        case IntegerLiteral literal -> "" + literal.value;
        case StringLiteral literal -> literal.string;
        default -> "token [%s]".formatted(token.getClass().getSimpleName());
      };
      case Byte character -> "%s character %s".formatted(
          isAscii(character)
              ? isPrintable(character)
                  ? "ascii"
                  : "non-printable ascii"
              : "non-ascii",
          isPrintable(character)
              ? "[%c]".formatted(character)
              : "with decimal value of %d"
                  .formatted(toUnsignedInt(character)));
      case String string -> string;
      case Variable variable -> variable.name;
      case Identifier identifier -> join("/", join(
          identifier.namespace.components,
          single(identifier.variable.name)));
      default -> arg.toString();
    };
  }
}
