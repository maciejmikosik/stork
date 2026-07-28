package com.mikosik.stork.common.text;

import static com.mikosik.stork.common.ImmutableList.join;
import static com.mikosik.stork.common.ImmutableList.list;
import static com.mikosik.stork.common.ImmutableList.none;
import static com.mikosik.stork.common.ImmutableList.single;
import static java.util.Objects.hash;

import java.util.List;

public class Outline {
  public final String text;
  public final List<Outline> branches;

  private Outline(
      String text,
      List<Outline> branches) {
    this.text = text;
    this.branches = branches;
  }

  public static Outline outline(String text) {
    return new Outline(text, none());
  }

  public Outline nest(List<Outline> branches) {
    return new Outline(text, join(this.branches, branches));
  }

  public Outline nest(Outline branch) {
    return nest(single(branch));
  }

  public Outline nest(Outline branchA, Outline branchB, Outline... branches) {
    return nest(list(branchA, branchB, branches));
  }

  public Outline nest(String leaf) {
    return nest(single(outline(leaf)));
  }

  public boolean equals(Object object) {
    return object instanceof Outline outline
        && text.equals(outline.text)
        && branches.equals(outline.branches);
  }

  public int hashCode() {
    return hash(text, branches);
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
    for (Outline branch : branches) {
      branch.appendTo(builder, indentation + 1);
    }
  }
}
