package com.mikosik.lang.tool;

import static com.mikosik.lang.common.Check.check;
import static com.mikosik.lang.common.Collections.first;
import static com.mikosik.lang.model.def.Definition.definition;
import static com.mikosik.lang.model.runtime.Application.application;
import static com.mikosik.lang.model.runtime.Lambda.lambda;
import static com.mikosik.lang.model.runtime.Variable.variable;
import static com.mikosik.lang.model.syntax.BracketType.ROUND;
import static com.mikosik.lang.model.syntax.Visitor.visit;

import com.mikosik.lang.model.def.Definition;
import com.mikosik.lang.model.runtime.Expression;
import com.mikosik.lang.model.syntax.Bracket;
import com.mikosik.lang.model.syntax.Sentence;
import com.mikosik.lang.model.syntax.Syntax;
import com.mikosik.lang.model.syntax.Visitor;
import com.mikosik.lang.model.syntax.Word;

public class Compiler {
  public static Definition compileDefinition(Sentence sentence) {
    return visit(sentence, new Visitor<Definition>() {
      protected Definition visitLabel(Word head, Sentence tail) {
        return definition(head.string, compileLambda(tail));
      }
    });
  }

  public static Expression compileExpression(Sentence sentence) {
    return visit(sentence, new Visitor<Expression>() {
      protected Expression visitLabel(Word head, Sentence tail) {
        return compileApplication(sentence);
      }

      protected Expression visitRound(Bracket head, Sentence tail) {
        return compileLambda(sentence);
      }
    });
  }

  public static Expression compileApplication(Sentence sentence) {
    return visit(sentence, new Visitor<Expression>() {
      protected Expression visitLabel(Word head, Sentence tail) {
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
    });
  }

  public static Expression compileLambda(Sentence sentence) {
    return visit(sentence, new Visitor<Expression>() {
      protected Expression visitRound(Bracket head, Sentence tail) {
        return lambda(
            parameterIn(head),
            compileLambda(tail));
      }

      protected Expression visitCurly(Bracket head, Sentence tail) {
        // TODO rest of sentence after bracket is ignored right now
        return compileExpression(head.sentence);
      }
    });
  }

  private static String parameterIn(Bracket bracket) {
    check(bracket.type == ROUND);
    check(bracket.sentence.parts.size() == 1);
    Word word = (Word) first(bracket.sentence.parts);
    return word.string;
  }
}
