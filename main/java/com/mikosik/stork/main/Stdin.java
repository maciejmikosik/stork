package com.mikosik.stork.main;

import static java.lang.String.format;

import java.io.InputStream;

import com.mikosik.stork.data.model.Expression;

public class Stdin implements Expression {
  public final InputStream input;

  private Stdin(InputStream input) {
    this.input = input;
  }

  public static Expression stdin(InputStream input) {
    return new Stdin(input);
  }

  public String toString() {
    return format("stdin(#%s)", input.hashCode());
  }
}
