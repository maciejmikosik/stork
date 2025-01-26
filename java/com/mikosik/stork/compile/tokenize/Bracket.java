package com.mikosik.stork.compile.tokenize;

import static com.mikosik.stork.common.Throwables.check;
import static com.mikosik.stork.compile.tokenize.Bracket.Shape.CURLY;
import static com.mikosik.stork.compile.tokenize.Bracket.Shape.ROUND;
import static com.mikosik.stork.compile.tokenize.Bracket.Side.LEFT;
import static com.mikosik.stork.compile.tokenize.Bracket.Side.RIGHT;

public enum Bracket implements Token {
  LEFT_ROUND_BRACKET(LEFT, ROUND, '('),
  RIGHT_ROUND_BRACKET(RIGHT, ROUND, ')'),
  LEFT_CURLY_BRACKET(LEFT, CURLY, '{'),
  RIGHT_CURLY_BRACKET(RIGHT, CURLY, '}');

  public final Side side;
  public final Shape shape;
  public final Byte character;

  private Bracket(Side side, Shape shape, char character) {
    this.side = side;
    this.shape = shape;
    this.character = (byte) character;
  }

  private static final Bracket[] index = index();

  private static Bracket[] index() {
    var array = new Bracket[255];
    for (Bracket bracket : Bracket.values()) {
      array[bracket.character] = bracket;
    }
    return array;
  }

  public static boolean isBracket(byte character) {
    return bracketFor(character) != null;
  }

  public static Bracket bracket(byte character) {
    check(isBracket(character));
    return bracketFor(character);
  }

  private static Bracket bracketFor(byte character) {
    return index[Byte.toUnsignedInt(character)];
  }

  public enum Side {
    LEFT, RIGHT
  }

  public enum Shape {
    ROUND, CURLY
  }
}
