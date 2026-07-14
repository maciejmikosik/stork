package com.mikosik.stork.problem.compile.link;

import com.mikosik.stork.model.Identifier;

public class UndefinedFunction extends CannotLink {
  public final Identifier location;
  public final Identifier undefined;

  private UndefinedFunction(
      Identifier location,
      Identifier undefined) {
    this.location = location;
    this.undefined = undefined;
  }

  public static UndefinedFunction undefinedFunction(
      Identifier location,
      Identifier undefined) {
    return new UndefinedFunction(
        location,
        undefined);
  }
}
