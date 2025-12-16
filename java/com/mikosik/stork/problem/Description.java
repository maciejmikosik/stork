package com.mikosik.stork.problem;

import static com.mikosik.stork.common.Sequence.sequenceOf;

import java.util.Objects;

import com.mikosik.stork.common.Sequence;

public class Description {
  public final String text;
  public final Sequence<Description> children;

  private Description(
      String text,
      Sequence<Description> children) {
    this.text = text;
    this.children = children;
  }

  public static Description description(String text) {
    return new Description(text, sequenceOf());
  }

  public static Description description(
      String text,
      Sequence<Description> children) {
    return new Description(text, children);
  }

  public boolean equals(Object object) {
    return object instanceof Description description
        && Objects.equals(toString(), description.toString());
  }

  public int hashCode() {
    return toString().hashCode();
  }

  public String toString() {
    var builder = new StringBuilder();
    appendTo(builder, 0);
    return builder.toString();
  }

  private void appendTo(StringBuilder builder, int indentation) {
    builder
        .append("    ".repeat(indentation))
        .append(text)
        .append("\n");
    for (Description child : children) {
      child.appendTo(builder, indentation + 1);
    }
  }
}
