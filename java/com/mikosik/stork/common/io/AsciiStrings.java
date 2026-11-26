package com.mikosik.stork.common.io;

import static com.mikosik.stork.common.err.Throwables.check;
import static java.nio.charset.StandardCharsets.US_ASCII;

public class AsciiStrings {
  public static boolean isAscii(String string) {
    return US_ASCII.newEncoder().canEncode(string);
  }

  public static String checkAscii(String string) {
    check(isAscii(string));
    return string;
  }

  public static byte[] tryToAsciiBytesOrFail(String string) {
    return checkAscii(string).getBytes(US_ASCII);
  }

  public static String tryToAsciiStringOrFail(byte... bytes) {
    return checkAscii(new String(bytes, US_ASCII));
  }
}
