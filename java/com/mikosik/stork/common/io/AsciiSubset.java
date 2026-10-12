package com.mikosik.stork.common.io;

import static java.lang.Byte.toUnsignedInt;
import static java.util.stream.IntStream.rangeClosed;

public class AsciiSubset {
  private final boolean[] mask;

  private AsciiSubset(boolean[] mask) {
    this.mask = mask;
  }

  public static AsciiSubsetBuilder asciiSubset() {
    return new AsciiSubsetBuilder();
  }

  public boolean contains(byte character) {
    return mask[toUnsignedInt(character)];
  }

  public static class AsciiSubsetBuilder {
    private final boolean[] mask = new boolean[256];

    private AsciiSubsetBuilder() {}

    public AsciiSubsetBuilder addRange(byte first, byte last) {
      rangeClosed(first, last)
          .forEach(character -> {
            mask[character] = true;
          });
      return this;
    }

    public AsciiSubsetBuilder addRange(char first, char last) {
      return addRange((byte) first, (byte) last);
    }

    public AsciiSubsetBuilder add(String characters) {
      characters.codePoints()
          .forEach(codePoint -> {
            mask[codePoint] = true;
          });
      return this;
    }

    public AsciiSubsetBuilder addLetters() {
      return addRange('a', 'z')
          .addRange('A', 'Z');
    }

    public AsciiSubsetBuilder addDigits() {
      return addRange('0', '9');
    }

    public AsciiSubsetBuilder addNumerics() {
      return addDigits().add("+-");
    }

    public AsciiSubsetBuilder addWhitespaces() {
      return add(" \n\r\t");
    }

    public AsciiSubset build() {
      return new AsciiSubset(mask);
    }
  }
}
