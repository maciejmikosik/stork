package com.mikosik.stork.problem.build.tokenize;

import static com.mikosik.stork.common.io.Ascii.isAscii;
import static com.mikosik.stork.common.io.Ascii.isPrintable;
import static com.mikosik.stork.problem.build.tokenize.IllegalCode.Location.ANYWHERE;
import static com.mikosik.stork.problem.build.tokenize.IllegalCode.Location.IN_STRING_LITERAL;
import static java.lang.Byte.toUnsignedInt;

public class IllegalCode implements CannotTokenize {
  public final byte code;
  public final Location location;

  private IllegalCode(byte code, Location location) {
    this.code = code;
    this.location = location;
  }

  public static IllegalCode illegalCode(byte code) {
    return new IllegalCode(code, ANYWHERE);
  }

  public static IllegalCode illegalCodeInStringLiteral(byte code) {
    return new IllegalCode(code, IN_STRING_LITERAL);
  }

  public String description() {
    var unsigned = toUnsignedInt(code);
    return """
        %s code %s
          dec  : %d
          hex  : %h
          char : %c
        """.formatted(
        isAscii(code)
            ? "illegal ascii"
            : "non-ascii",
        location.name,
        unsigned,
        unsigned,
        isPrintable(code) ? code : ' ');
  }

  public enum Location {
    ANYWHERE(""), IN_STRING_LITERAL("in string literal");

    private final String name;

    Location(String name) {
      this.name = name;
    }
  }
}
