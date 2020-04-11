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

      protected Expression visitInteger(Word head, Sentence tail) {
        if (tail.parts.available()) {
          throw new RuntimeException("integer cannot be followed by sentence");
        } else {
          return primitive(new BigInteger(head.string));
        }
      }

      protected Expression visitRound(Bracket head, Sentence tail) {
        return compileLambda(sentence);
      }
    });
  }

  private static Expression compileApplication(Sentence sentence) {
    return visit(sentence, new Visitor<Expression>() {
      protected Expression visitLabel(Word head, Sentence tail) {
        Expression expression = variable(head.string);
        for (Syntax part : tail.parts) {
          Bracket bracket = (Bracket) part;
          check(bracket.type == ROUND);
          expression = application(
              expression,
              compileExpression(bracket.sentence));
        }
        return expression;
      }
    });
  }

  private static Expression compileLambda(Sentence sentence) {
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
    check(!bracket.sentence.parts.tail().available());
    Word word = (Word) bracket.sentence.parts.head();
    return word.string;
  }
}
