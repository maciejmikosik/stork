package com.mikosik.lang.compile;

import static com.mikosik.lang.model.Alias.alias;
import static com.mikosik.lang.model.Application.application;

import java.util.List;

import com.mikosik.lang.model.Expression;

public class Compiler {
  public static Expression compileApplication(List<Syntax> syntax) {
    Expression expression = alias(((Word) syntax.get(0)).string);
    for (int index = 1; index < syntax.size(); index++) {
      Bracket bracket = (Bracket) syntax.get(index);
      expression = application(
          expression,
          compileApplication(bracket.children));
    }
    return expression;
  }
}
