package com.mikosik.lang.compile;

import static com.mikosik.lang.model.Alias.alias;
import static com.mikosik.lang.model.Application.application;
import static java.util.stream.Collectors.toList;

import java.util.List;

import com.mikosik.lang.model.Expression;

public class Expresser {
  public static Expression expression(Syntax syntax) {
    List<Expression> children = syntax.children.stream()
        .map(Expresser::expression)
        .collect(toList());
    Expression result = children.remove(0);
    while (children.size() > 0) {
      result = application(result, children.remove(0));
    }
    return result;
  }

  private static Expression expression(Object child) {
    if (child instanceof Syntax) {
      return expression((Syntax) child);
    } else if (child instanceof String) {
      return alias((String) child);
    } else {
      throw new RuntimeException();
    }
  }
}
