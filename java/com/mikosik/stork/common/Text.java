package com.mikosik.stork.common;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

import java.util.LinkedList;
import java.util.List;

public class Text {
  private final List<Object> items = new LinkedList<Object>();

  public static Text text() {
    return new Text();
  }

  public Text line(Object... items) {
    this.items.addAll(asList(items));
    this.items.add("\n");
    return this;
  }

  public String toString() {
    return items.stream()
        .map(Object::toString)
        .collect(joining());
  }

  public void stdout() {
    System.out.print(toString());
  }

  public void stderr() {
    System.err.print(toString());
  }
}
