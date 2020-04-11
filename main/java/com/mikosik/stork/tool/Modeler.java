package com.mikosik.stork.tool;

import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.data.model.Application.application;
import static com.mikosik.stork.data.model.Definition.definition;
import static com.mikosik.stork.data.model.Lambda.lambda;
import static com.mikosik.stork.data.model.Primitive.primitive;
import static com.mikosik.stork.data.model.Variable.variable;
import static com.mikosik.stork.data.syntax.BracketType.ROUND;
import static com.mikosik.stork.data.syntax.Visitor.visit;

import java.math.BigInteger;

import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.syntax.Bracket;
import com.mikosik.stork.data.syntax.Sentence;
import com.mikosik.stork.data.syntax.Syntax;
import com.mikosik.stork.data.syntax.Visitor;
import com.mikosik.stork.data.syntax.Word;

public class Modeler {
  public static Definition modelDefinition(Sentence sentence) {
    return visit(sentence, new Visitor<Definition>() {
      protected Definition visitLabel(Word head, Sentence tail) {
        return definition(head.string, modelLambda(tail));
      }
    });
  }

  public static Expression modelExpression(Sentence sentence) {
    return visit(sentence, new Visitor<Expression>() {
      protected Expression visitLabel(Word head, Sentence tail) {
        return modelApplication(sentence);
      }

      protected Expression visitInteger(Word head, Sentence tail) {
        if (tail.parts.available()) {
          throw new RuntimeException("integer cannot be followed by sentence");
        } else {
          return primitive(new BigInteger(head.string));
        }
      }

      protected Expression visitRound(Bracket head, Sentence tail) {
        return modelLambda(sentence);
      }
    });
  }

  private static Expression modelApplication(Sentence sentence) {
    return visit(sentence, new Visitor<Expression>() {
      protected Expression visitLabel(Word head, Sentence tail) {
        Expression expression = variable(head.string);
        for (Syntax part : tail.parts) {
          Bracket bracket = (Bracket) part;
          check(bracket.type == ROUND);
          expression = application(
              expression,
              modelExpression(bracket.sentence));
        }
        return expression;
      }
    });
  }

  private static Expression modelLambda(Sentence sentence) {
    return visit(sentence, new Visitor<Expression>() {
      protected Expression visitRound(Bracket head, Sentence tail) {
        return lambda(
            parameterIn(head),
            modelLambda(tail));
      }

      protected Expression visitCurly(Bracket head, Sentence tail) {
        // TODO rest of sentence after bracket is ignored right now
        return modelExpression(head.sentence);
      }
    });
  }

  private static String parameterIn(Bracket bracket) {
    check(bracket.type == ROUND);
    check(!bracket.sentence.parts.tail().available());
    Word word = (Word) bracket.sentence.parts.head();
    return word.string;
  }
}
