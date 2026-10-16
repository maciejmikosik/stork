package com.mikosik.stork.compile.err;

import static com.mikosik.stork.common.col.ImmutableList.join;
import static com.mikosik.stork.common.col.ImmutableList.single;
import static com.mikosik.stork.common.io.Ascii.isAscii;
import static com.mikosik.stork.common.io.Ascii.isPrintable;
import static com.mikosik.stork.common.text.Outline.outline;
import static java.lang.Byte.toUnsignedInt;
import static java.lang.String.format;
import static java.lang.String.join;

import java.util.List;

import com.mikosik.stork.common.text.Outline;
import com.mikosik.stork.model.exp.Namespace;
import com.mikosik.stork.model.exp.Reference;
import com.mikosik.stork.model.exp.Variable;
import com.mikosik.stork.model.token.IntegerLiteral;
import com.mikosik.stork.model.token.Label;
import com.mikosik.stork.model.token.Punctuator;
import com.mikosik.stork.model.token.StringLiteral;

public class Describer {
  public static Outline describe(CompilerException exception) {
    return outline("cannot compile")
        .nest(exception.problems.stream()
            .map(Describer::describe)
            .toList());
  }

  private static Outline describe(Problem problem) {
    return outline(problem.name)
        .nest(problem.description.entrySet().stream()
            .map(entry -> describeEntry(
                entry.getKey(),
                entry.getValue()))
            .toList());
  }

  private static Outline describeEntry(String key, Object value) {
    return switch (value) {
      case Byte character -> outline(key + ":")
          .nest(formatCharacter(character));
      case List<?> list -> outline(key + ":")
          .nest(list.stream()
              .map(Describer::formatValue)
              .map(Outline::outline)
              .toList());
      default -> outline(format(
          "%s: %s",
          key,
          formatValue(value)));
    };
  }

  private static String formatValue(Object arg) {
    return switch (arg) {
      case Label label -> label.string;
      case Punctuator punctuator -> switch (punctuator) {
        case LEFT_ROUND_BRACKET -> "(";
        case RIGHT_ROUND_BRACKET -> ")";
        case LEFT_CURLY_BRACKET -> "{";
        case RIGHT_CURLY_BRACKET -> "}";
        case DOT -> ".";
      };
      case IntegerLiteral literal -> "" + literal.value;
      case StringLiteral literal -> literal.string;
      case String string -> string;
      case Variable variable -> variable.name;
      case Namespace namespace -> join("/", namespace.components);
      // TODO create utils for formatting and parsing
      case Reference reference -> join("/", join(
          reference.namespace.components,
          single(reference.variable.name)));
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
