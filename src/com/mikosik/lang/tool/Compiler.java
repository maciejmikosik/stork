package com.mikosik.lang.tool;

import static com.mikosik.lang.model.runtime.Application.application;
import static com.mikosik.lang.model.runtime.Variable.variable;

import com.mikosik.lang.model.runtime.Expression;
import com.mikosik.lang.model.syntax.Bracket;
import com.mikosik.lang.model.syntax.Sentence;
import com.mikosik.lang.model.syntax.Word;

public class Compiler {
  public static Expression compileApplication(Sentence sentence) {
    Expression expression = variable(((Word) sentence.parts.get(0)).string);
    for (int index = 1; index < sentence.parts.size(); index++) {
      Bracket bracket = (Bracket) sentence.parts.get(index);
      expression = application(
          expression,
          compileApplication(bracket.sentence));
    }
    return expression;
  }
}
