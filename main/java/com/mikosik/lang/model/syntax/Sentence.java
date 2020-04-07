package com.mikosik.lang.model.syntax;

import static java.util.stream.Collectors.joining;

import java.util.List;

public class Sentence {
  public final List<Syntax> parts;

  private Sentence(List<Syntax> parts) {
    this.parts = parts;
  }

  public static Sentence sentence(List<Syntax> parts) {
    return new Sentence(parts);
  }

  public String toString() {
    return parts.stream()
        .map(Syntax::toString)
        .collect(joining());
  }
}
