package com.mikosik.lang.tool;

import static com.mikosik.lang.common.Check.check;
import static com.mikosik.lang.common.Collections.first;
import static com.mikosik.lang.common.Collections.skipFirst;
import static com.mikosik.lang.model.def.Definition.definition;
import static com.mikosik.lang.model.runtime.Application.application;
import static com.mikosik.lang.model.runtime.Lambda.lambda;
import static com.mikosik.lang.model.runtime.Variable.variable;
import static com.mikosik.lang.model.syntax.BracketType.CURLY;
import static com.mikosik.lang.model.syntax.BracketType.ROUND;
import static com.mikosik.lang.model.syntax.Sentence.sentence;

import com.mikosik.lang.model.def.Definition;
import com.mikosik.lang.model.runtime.Expression;
import com.mikosik.lang.model.syntax.Bracket;
import com.mikosik.lang.model.syntax.Sentence;
import com.mikosik.lang.model.syntax.Syntax;
import com.mikosik.lang.model.syntax.Word;

public class Compiler {
  public static Definition compileDefinition(Sentence sentence) {
    return definition(
        ((Word) first(sentence.parts)).string,
        compileLambda(sentence(skipFirst(sentence.parts))));
  }

  public static Expression compileExpression(Sentence sentence) {
    if (first(sentence.parts) instanceof Word) {
      return compileApplication(sentence);
    } else if (first(sentence.parts) instanceof Bracket) {
      return compileLambda(sentence);
    } else {
      throw new RuntimeException();
    }
  }

  public static Expression compileApplication(Sentence sentence) {
    return compileApplication(
        (Word) first(sentence.parts),
        sentence(skipFirst(sentence.parts)));
  }

  private static Expression compileApplication(Word head, Sentence tail) {
    Expression expression = variable(head.string);
    for (Syntax part : tail.parts) {
      Bracket bracket = (Bracket) part;
      check(bracket.type == ROUND);
      expression = application(
          expression,
          compileApplication(bracket.sentence));
    }
    return expression;
  }

  public static Expression compileLambda(Sentence sentence) {
    return compileLambda(
        (Bracket) first(sentence.parts),
        sentence(skipFirst(sentence.parts)));
  }

  public static Expression compileLambda(Bracket head, Sentence tail) {
    Bracket bracket = head;
    if (bracket.type == ROUND) {
      return lambda(
          parameterIn(bracket),
          compileLambda(tail));
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
    Word word = (Word) first(bracket.sentence.parts);
    return word.string;
  }
}
