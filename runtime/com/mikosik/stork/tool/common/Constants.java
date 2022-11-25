package com.mikosik.stork.tool.common;

import static com.mikosik.stork.model.Identifier.identifier;

import com.mikosik.stork.model.Identifier;

public class Constants {
  public static final Identifier TRUE = identifier("stork.boolean.true");
  public static final Identifier FALSE = identifier("stork.boolean.false");
  public static final Identifier SOME = identifier("stork.stream.some");
  public static final Identifier NONE = identifier("stork.stream.none");

  public static final Identifier END_OF_STREAM = identifier("stork.program.END_OF_STREAM");
}
