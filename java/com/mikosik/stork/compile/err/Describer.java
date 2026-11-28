package com.mikosik.stork.compile.err;

import static com.mikosik.stork.common.io.Ascii.isAscii;
import static com.mikosik.stork.common.io.Ascii.isPrintable;
import static com.mikosik.stork.common.text.Outline.outline;
import static com.mikosik.stork.compile.Decompiler.decompile;
import static java.lang.Byte.toUnsignedInt;
import static java.lang.String.format;

import java.util.List;

import com.mikosik.stork.common.text.Outline;
import com.mikosik.stork.model.exp.Phrase;
import com.mikosik.stork.model.token.Identifier;
import com.mikosik.stork.model.token.IntegerLiteral;
import com.mikosik.stork.model.token.Punctuator;
import com.mikosik.stork.model.token.StringLiteral;

public class Describer {
  public static Outline describe(CompilerException exception) {
    return outline("cannot compile")
        .nest(exception.miscompilations.stream()
            .map(Describer::describe)
            .toList());
  }

  private static Outline describe(Miscompilation miscompilation) {
    return outline(miscompilation.name)
        .nest(miscompilation.attributes.entrySet().stream()
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
      case Phrase phrase -> decompile(phrase);
      case Identifier identifier -> identifier.string;
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
