package com.mikosik.lang.compile;

import static java.util.Arrays.asList;

import java.util.LinkedList;
import java.util.List;

public class Syntaxer {
  public static Syntax syntax(String source, Scope scope) {

    if (scope.children.isEmpty()) {
      String token = source.substring(scope.iOpening + 1, scope.iClosing);
      // TODO empty token means empty brackets, what to do then?
      return Syntax.syntax(asList(token.trim()));
    }

    List<Object> children = new LinkedList<>();

    String firstToken = source.substring(
        scope.iOpening + 1,
        scope.children.get(0).iOpening);
    addIfNotEmpty(children, firstToken);

    for (int i = 0; i < scope.children.size() - 1; i++) {
      children.add(syntax(source, scope.children.get(i)));

      String token = source.substring(
          scope.children.get(i).iClosing + 1,
          scope.children.get(i + 1).iOpening);
      addIfNotEmpty(children, token);
    }

    children.add(syntax(source, last(scope.children)));
    String lastToken = source.substring(
        last(scope.children).iClosing + 1,
        scope.iClosing);
    addIfNotEmpty(children, lastToken);

    return Syntax.syntax(children);
  }

  private static <E> E last(List<E> elements) {
    return elements.get(elements.size() - 1);
  }

  private static void addIfNotEmpty(List<? super String> list, String string) {
    String trimmed = string.trim();
    if (!trimmed.isEmpty()) {
      list.add(trimmed);
    }
  }
}
