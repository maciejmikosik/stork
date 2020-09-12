package com.mikosik.stork.main;

import static java.lang.String.format;

import com.mikosik.stork.data.model.Expression;

public class Streamed implements Expression {
  public final int oneByte;

  private Streamed(int oneByte) {
    this.oneByte = oneByte;
  }

  public static Streamed streamed(int oneByted) {
    return new Streamed(oneByted);
  }

  public String toString() {
    return format("streamed(%s)", oneByte);
  }
}
