package com.mikosik.lang.compile;

import static com.mikosik.lang.model.Alias.alias;
import static com.mikosik.lang.model.Application.application;

import com.mikosik.lang.model.Expression;

public class Compiler {
  public static Expression compileApplication(Sentence sentence) {
    Expression expression = alias(((Word) sentence.parts.get(0)).string);
    for (int index = 1; index < sentence.parts.size(); index++) {
      Bracket bracket = (Bracket) sentence.parts.get(index);
      expression = application(
          expression,
          compileApplication(bracket.sentence));
    }
    return expression;
  }
}
