package com.mikosik.stork.problem;

import static com.mikosik.stork.common.Description.description;
import static com.mikosik.stork.common.ImmutableList.join;
import static com.mikosik.stork.common.ImmutableList.single;
import static com.mikosik.stork.common.io.Ascii.isAscii;
import static com.mikosik.stork.common.io.Ascii.isPrintable;
import static com.mikosik.stork.common.text.Templater.templater;
import static java.lang.Byte.toUnsignedInt;
import static java.lang.String.join;

import com.mikosik.stork.common.Description;
import com.mikosik.stork.common.text.Templater;
import com.mikosik.stork.compile.tokenize.Bracket;
import com.mikosik.stork.compile.tokenize.IntegerLiteral;
import com.mikosik.stork.compile.tokenize.Label;
import com.mikosik.stork.compile.tokenize.StringLiteral;
import com.mikosik.stork.compile.tokenize.Symbol;
import com.mikosik.stork.compile.tokenize.Token;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Variable;
import com.mikosik.stork.problem.compile.CannotCompile;
import com.mikosik.stork.problem.compute.CannotCompute;
import com.mikosik.stork.problem.compute.FunctionMissing;

public class Describer {
  private static final ProblemTemplates templates = new ProblemTemplates();
  private static final Templater templater = templater(Describer::formatArgument);

  public static Description describe(CannotCompile problem) {
    return description(templater
        .template(templates.get(problem))
        .model(problem));
  }

  public static Description describe(CannotCompute cannotCompute) {
    return switch (cannotCompute) {
      case FunctionMissing problem -> description(templater
          .template("function {function} is missing")
          .model(problem));
      default -> description("cannot compute");
    };
  }

  private static String formatArgument(Object arg) {
    return switch (arg) {
      case Token token -> switch (token) {
        case Label label -> "label [%s]".formatted(label.string);
        case Bracket bracket -> "bracket [%c]".formatted(bracket.character);
        case Symbol symbol -> "symbol [%c]".formatted(symbol.character);
        case IntegerLiteral literal -> "integer [%s]".formatted(literal.value);
        case StringLiteral literal -> "string [%s]".formatted(literal.string);
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
              : "with decimal value of [%d]"
                  .formatted(toUnsignedInt(character)));
      case String string -> bracket(string);
      case Variable variable -> bracket(variable.name);
      case Identifier identifier -> bracket(join("/", join(
          identifier.namespace.components,
          single(identifier.variable.name))));
      default -> arg.toString();
    };
  }

  private static String bracket(String string) {
    return "[" + string + "]";
  }
}
