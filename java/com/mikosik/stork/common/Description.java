package com.mikosik.stork.common;

import static com.mikosik.stork.common.ImmutableList.none;
import static java.util.Objects.hash;

import java.util.List;

public class Description {
  public final String text;
  public final List<Description> children;

  private Description(
      String text,
      List<Description> children) {
    this.text = text;
    this.children = children;
  }

  public static Description description(String text) {
    return new Description(text, none());
  }

  public static Description description(
      String text,
      List<Description> children) {
    return new Description(text, children);
  }

  public boolean equals(Object object) {
    return object instanceof Description description
        && text.equals(description.text)
        && children.equals(description.children);
  }

  public int hashCode() {
    return hash(text, children);
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
