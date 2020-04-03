package com.mikosik.lang.tool;

import static com.mikosik.lang.common.Check.check;
import static com.mikosik.lang.common.Collections.first;
import static com.mikosik.lang.common.Collections.skipFirst;
import static com.mikosik.lang.model.runtime.Application.application;
import static com.mikosik.lang.model.runtime.Lambda.lambda;
import static com.mikosik.lang.model.runtime.Variable.variable;
import static com.mikosik.lang.model.syntax.BracketType.CURLY;
import static com.mikosik.lang.model.syntax.BracketType.ROUND;
import static com.mikosik.lang.model.syntax.Sentence.sentence;

import com.mikosik.lang.model.runtime.Expression;
import com.mikosik.lang.model.syntax.Bracket;
import com.mikosik.lang.model.syntax.Sentence;
import com.mikosik.lang.model.syntax.Word;

public class Compiler {
  public static Expression compileExpression(Sentence sentence) {
    return sentence.parts.get(0) instanceof Word
        ? compileApplication(sentence)
        : compileLambda(sentence);
  }

  public static Expression compileApplication(Sentence sentence) {
    Expression expression = variable(((Word) sentence.parts.get(0)).string);
    for (int index = 1; index < sentence.parts.size(); index++) {
      Bracket bracket = (Bracket) sentence.parts.get(index);
      check(bracket.type == ROUND);
      expression = application(
          expression,
          compileApplication(bracket.sentence));
    }
    return expression;
  }

  public static Expression compileLambda(Sentence sentence) {
    Bracket bracket = (Bracket) first(sentence.parts);
    if (bracket.type == ROUND) {
      return lambda(
          parameterIn(bracket),
          compileLambda(sentence(skipFirst(sentence.parts))));
    } else if (bracket.type == CURLY) {
      // TODO rest of sentence after bracket is ignored right now
      return compileExpression(bracket.sentence);
    } else {
      throw new RuntimeException();
    }
  }

  private static String parameterIn(Bracket bracket) {
    check(bracket.type == ROUND);
    check(bracket.sentence.parts.size() == 1);
    Word word = (Word) bracket.sentence.parts.get(0);
    return word.string;
  }
}
