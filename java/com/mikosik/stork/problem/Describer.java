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

import java.lang.reflect.Field;

import com.mikosik.stork.common.text.Outline;
import com.mikosik.stork.model.exp.Identifier;
import com.mikosik.stork.model.exp.Variable;
import com.mikosik.stork.model.token.Bracket;
import com.mikosik.stork.model.token.IntegerLiteral;
import com.mikosik.stork.model.token.Label;
import com.mikosik.stork.model.token.StringLiteral;
import com.mikosik.stork.model.token.Symbol;
import com.mikosik.stork.model.token.Token;
import com.mikosik.stork.problem.compile.CompilerException;
import com.mikosik.stork.problem.compute.ComputerException;

public class Describer {
  public static Outline describe(CompilerException exception) {
    var descriptions = exception.problems.stream()
        .map(Describer::describe)
        .toList();
    return descriptions.size() == 1
        ? descriptions.getFirst()
        : outline("cannot compile")
            .nest(descriptions);
  }

  public static Outline describe(ComputerException exception) {
    return describe(exception.problem);
  }

  private static Outline describe(Object problem) {
    return outline(problem.getClass().getSimpleName())
        .nest(stream(problem.getClass().getFields())
            .map(field -> formatField(problem, field))
            .toList());
  }

  private static Outline formatField(Object problem, Field field) {
    var fieldValue = read(field, problem);
    return switch (fieldValue) {
      case Byte character -> outline(field.getName() + ":")
          .nest(formatCharacter(character));
      default -> outline(format(
          "%s: %s",
          field.getName(),
          formatFieldValue(fieldValue)));
    };
  }

  private static String formatFieldValue(Object arg) {
    return switch (arg) {
      case Token token -> switch (token) {
        case Label label -> label.string;
        case Bracket bracket -> "" + (char) bracket.character;
        case Symbol symbol -> "" + (char) symbol.character;
        case IntegerLiteral literal -> "" + literal.value;
        case StringLiteral literal -> literal.string;
        default -> "token [%s]".formatted(token.getClass().getSimpleName());
      };
      case String string -> string;
      case Variable variable -> variable.name;
      case Identifier identifier -> join("/", join(
          identifier.namespace.components,
          single(identifier.variable.name)));
      default -> arg.toString();
    };
  }

  private static Outline formatCharacter(byte character) {
    var dec = toUnsignedInt(character);
    return isAscii(character)
        ? isPrintable(character)
            ? outline("ascii")
                .nest("printed: [%c]".formatted(character))
                .nest("decimal: %d".formatted(dec))
            : outline("non-printable ascii")
                .nest("decimal: %d".formatted(dec))
        : outline("non-ascii")
            .nest("decimal: %d".formatted(dec));
  }
}
