package com.mikosik.stork.problem;

import static com.mikosik.stork.common.Description.description;
import static com.mikosik.stork.common.io.Ascii.isAscii;
import static com.mikosik.stork.common.io.Ascii.isPrintable;
import static java.lang.Byte.toUnsignedInt;
import static java.lang.String.format;

import com.mikosik.stork.common.Description;
import com.mikosik.stork.compile.tokenize.Bracket;
import com.mikosik.stork.compile.tokenize.IntegerLiteral;
import com.mikosik.stork.compile.tokenize.Label;
import com.mikosik.stork.compile.tokenize.StringLiteral;
import com.mikosik.stork.compile.tokenize.Symbol;
import com.mikosik.stork.compile.tokenize.Token;
import com.mikosik.stork.problem.compile.CannotCompile;
import com.mikosik.stork.problem.compile.importing.CannotImport;
import com.mikosik.stork.problem.compile.importing.IllegalCharacter;
import com.mikosik.stork.problem.compile.link.CannotLink;
import com.mikosik.stork.problem.compile.link.FunctionDefinedMoreThanOnce;
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
          "function [%s] is missing".formatted(problem.function.name()));
      default -> description("cannot compute");
    };
  }

  private static Description describe(CannotImport cannotImport) {
    return description(switch (cannotImport) {
      case IllegalCharacter problem -> format(
          "import [%s] contains illegal %s",
          problem.text, describe(problem.character));
      default -> throw new RuntimeException();
    });
  }

  private static Description describe(CannotTokenize cannotTokenize) {
    return description(switch (cannotTokenize) {
      case IllegalCharacterInString problem -> format(
          "string contains illegal %s",
          describe(problem.character));
      case IllegalCharacterInCode problem -> format(
          "code contains illegal %s",
          describe(problem.character));
      default -> throw new RuntimeException();
    });
  }

  private static Description describe(CannotParse cannotParse) {
    return description(switch (cannotParse) {
      case UnexpectedToken problem -> format(
          "unexpected %s",
          describe(problem.token));
      default -> throw new RuntimeException();
    });
  }

  private static Description describe(CannotLink cannotLink) {
    return description(switch (cannotLink) {
      case FunctionDefinedMoreThanOnce problem -> format(
          "function [%s] is defined more than once",
          problem.function.name());
      case FunctionNotDefined problem -> format(
          "function [%s] imports undefined function [%s]",
          problem.location.name(), problem.undefined.name());
      case VariableCannotBeBound problem -> format(
          "function [%s] uses undefined variable [%s]",
          problem.location.name(), problem.variable.name);
      default -> throw new RuntimeException();
    });
  }

  private static String describe(Token token) {
    return switch (token) {
      case Label label -> "label [%s]".formatted(label.string);
      case Bracket bracket -> "bracket [%c]".formatted(bracket.character);
      case Symbol symbol -> "symbol [%c]".formatted(symbol.character);
      case IntegerLiteral literal -> "integer [%s]".formatted(literal.value);
      case StringLiteral literal -> "string [%s]".formatted(literal.string);
      default -> "token [%s]".formatted(token.getClass().getSimpleName());
    };
  }

  private static String describe(byte character) {
    return "%s character %s".formatted(
        isAscii(character)
            ? isPrintable(character)
                ? "ascii"
                : "non-printable ascii"
            : "non-ascii",
        isPrintable(character)
            ? "[%c]".formatted(character)
            : "with decimal value of [%d]"
                .formatted(toUnsignedInt(character)));
  }
}
