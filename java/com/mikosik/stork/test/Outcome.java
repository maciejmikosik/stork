package com.mikosik.stork.test;

public class Outcome {
  public final Object object;

  private Outcome(Object object) {
    this.object = object;
  }

  public static Outcome outcome(Object object) {
    return new Outcome(object);
  }
}
