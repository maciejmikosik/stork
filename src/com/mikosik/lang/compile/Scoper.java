package com.mikosik.lang.compile;

import java.util.LinkedList;
import java.util.List;

public class Scoper {
  public static Scope scope(String source) {
    return scope(source, -1);
  }

  private static Scope scope(String source, int iOpening) {
    Scope scope = inside(source, iOpening);
    // TODO check if types of brackets match
    return scope;
  }

  private static Scope inside(String source, int iOpening) {
    List<Scope> children = new LinkedList<>();

    for (int index = iOpening + 1; index < source.length(); index++) {
      // TODO check other types of brackets
      char character = source.charAt(index);
      if (character == ')') {
        return Scope.scope(iOpening, index, children);
      } else if (character == '(') {
        Scope child = scope(source, index);
        children.add(child);
        index = child.iClosing;
      }
    }
    return Scope.scope(iOpening, source.length(), children);
  }
}
