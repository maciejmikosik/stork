package com.mikosik.lang.model.syntax;

import java.util.List;

public class Sentence implements Syntax {
  public final List<Syntax> parts;

  private Sentence(List<Syntax> parts) {
    this.parts = parts;
  }

  public static Sentence sentence(List<Syntax> parts) {
    return new Sentence(parts);
  }
}
