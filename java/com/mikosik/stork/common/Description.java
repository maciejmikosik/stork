package com.mikosik.stork.common;

import static com.mikosik.stork.common.ImmutableList.join;
import static com.mikosik.stork.common.ImmutableList.list;
import static com.mikosik.stork.common.ImmutableList.none;
import static com.mikosik.stork.common.ImmutableList.single;
import static com.mikosik.stork.common.ImmutableList.toList;
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

  public Description children(List<Description> children) {
    return new Description(text, join(this.children, children));
  }

  public Description child(Description child) {
    return children(single(child));
  }

  public Description children(
      Description childA,
      Description childB,
      Description... children) {
    return children(join(
        list(childA, childB),
        toList(children)));
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
