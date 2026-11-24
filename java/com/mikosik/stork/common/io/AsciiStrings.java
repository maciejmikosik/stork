package com.mikosik.stork.common.io;

import static com.mikosik.stork.common.err.Throwables.check;
import static java.nio.charset.StandardCharsets.US_ASCII;

public class AsciiStrings {
  public static boolean isAscii(String string) {
    return US_ASCII.newEncoder().canEncode(string);
  }

  public static byte[] tryToAsciiBytesOrFail(String string) {
    check(isAscii(string));
    return string.getBytes(US_ASCII);
  }
}
