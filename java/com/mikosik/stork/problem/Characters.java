package com.mikosik.stork.problem;

import static com.mikosik.stork.common.io.Ascii.isAscii;
import static com.mikosik.stork.common.io.Ascii.isPrintable;
import static java.lang.Byte.toUnsignedInt;

public class Characters {
  public static String describeCharacter(byte character) {
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
