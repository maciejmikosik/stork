package com.mikosik.stork.problem;

import java.util.Objects;

public class Description {
  public final String text;

  private Description(String text) {
    this.text = text;
  }

  public static Description description(String text) {
    return new Description(text);
  }

  public boolean equals(Object object) {
    return object instanceof Description description
        && Objects.equals(toString(), description.toString());
  }

  public int hashCode() {
    return toString().hashCode();
  }

  public String toString() {
    return text;
  }
}
