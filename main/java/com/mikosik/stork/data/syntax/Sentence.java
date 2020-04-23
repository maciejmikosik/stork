package com.mikosik.stork.data.syntax;

import static com.mikosik.stork.common.Chains.stream;
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
    return stream(parts)
        .map(Syntax::toString)
        .collect(joining());
  }
}
