package com.mikosik.stork.data.syntax;

import static java.util.stream.Collectors.joining;

import com.mikosik.stork.common.Chain;

public class Sentence {
  public final Chain<Syntax> parts;

  private Sentence(Chain<Syntax> parts) {
    this.parts = parts;
  }

  public static Sentence sentence(Chain<Syntax> parts) {
    return new Sentence(parts);
  }

  public String toString() {
    return parts.stream()
        .map(Syntax::toString)
        .collect(joining());
  }
}
