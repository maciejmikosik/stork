package com.mikosik.stork.problem;

import static com.mikosik.stork.common.Collections.eachCell;
import static com.mikosik.stork.common.Description.description;
import static com.mikosik.stork.common.ImmutableList.join;
import static com.mikosik.stork.common.ImmutableList.single;
import static com.mikosik.stork.common.io.Ascii.isAscii;
import static com.mikosik.stork.common.io.Ascii.isPrintable;
import static java.lang.Byte.toUnsignedInt;
import static java.lang.String.join;

import com.mikosik.stork.common.Description;
import com.mikosik.stork.compile.tokenize.Bracket;
import com.mikosik.stork.compile.tokenize.IntegerLiteral;
import com.mikosik.stork.compile.tokenize.Label;
import com.mikosik.stork.compile.tokenize.StringLiteral;
import com.mikosik.stork.compile.tokenize.Symbol;
import com.mikosik.stork.compile.tokenize.Token;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Variable;
import com.mikosik.stork.problem.compile.CannotCompile;
import com.mikosik.stork.problem.compile.importing.CannotImport;
import com.mikosik.stork.problem.compile.importing.IllegalCharacter;
import com.mikosik.stork.problem.compile.link.CannotLink;
import com.mikosik.stork.problem.compile.link.DuplicatedFunction;
import com.mikosik.stork.problem.compile.link.FunctionNotDefined;
import com.mikosik.stork.problem.compile.link.VariableCannotBeBound;
import com.mikosik.stork.problem.compile.parse.CannotParse;
import com.mikosik.stork.problem.compile.parse.UnexpectedToken;
import com.mikosik.stork.problem.compile.tokenize.CannotTokenize;
import com.mikosik.stork.problem.compile.tokenize.IllegalCharacterInCode;
import com.mikosik.stork.problem.compile.tokenize.IllegalCharacterInString;
import com.mikosik.stork.problem.compute.CannotCompute;
import com.mikosik.stork.problem.compute.FunctionMissing;

public class Describe {
  public static Description describe(CannotCompile cannotCompile) {
    return switch (cannotCompile) {
      case CannotImport cannotImport -> describe(cannotImport);
      case CannotTokenize cannotTokenize -> describe(cannotTokenize);
      case CannotParse cannotParse -> describe(cannotParse);
      case CannotLink cannotLink -> describe(cannotLink);
      default -> throw new RuntimeException(cannotCompile.getClass().toString());
    };
  }

  public static Description describe(CannotCompute cannotCompute) {
    return switch (cannotCompute) {
      case FunctionMissing problem -> description(
          format("function [%s] is missing", problem.function));
      default -> description("cannot compute");
    };
  }

  private static Description describe(CannotImport cannotImport) {
    return description(switch (cannotImport) {
      case IllegalCharacter problem -> format(
          "import [%s] contains illegal %s",
          problem.text, problem.character);
      default -> throw new RuntimeException();
    });
  }

  private static Description describe(CannotTokenize cannotTokenize) {
    return description(switch (cannotTokenize) {
      case IllegalCharacterInString problem -> format(
          "string contains illegal %s",
          problem.character);
      case IllegalCharacterInCode problem -> format(
          "code contains illegal %s",
          problem.character);
      default -> throw new RuntimeException();
    });
  }

  private static Description describe(CannotParse cannotParse) {
    return description(switch (cannotParse) {
      case UnexpectedToken problem -> format(
          "unexpected %s",
          problem.token);
      default -> throw new RuntimeException();
    });
  }

  private static Description describe(CannotLink cannotLink) {
    return description(switch (cannotLink) {
      case DuplicatedFunction problem -> format(
          "function [%s] is defined more than once",
          problem.function);
      case FunctionNotDefined problem -> format(
          "function [%s] imports undefined function [%s]",
          problem.location, problem.undefined);
      case VariableCannotBeBound problem -> format(
          "function [%s] uses undefined variable [%s]",
          problem.location, problem.variable);
      default -> throw new RuntimeException();
    });
  }

  public static String format(String format, Object... args) {
    return String.format(format, eachCell(Describe::formatArg).apply(args));
  }

  private static Object formatArg(Object arg) {
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
      case Variable variable -> variable.name;
      case Identifier identifier -> join("/", join(
          identifier.namespace.components,
          single(identifier.variable.name)));
      default -> arg;
    };
  }
}
