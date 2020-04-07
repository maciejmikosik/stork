package com.mikosik.stork.tool;

import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.common.Collections.first;
import static com.mikosik.stork.model.def.Definition.definition;
import static com.mikosik.stork.model.runtime.Application.application;
import static com.mikosik.stork.model.runtime.Lambda.lambda;
import static com.mikosik.stork.model.runtime.Primitive.primitive;
import static com.mikosik.stork.model.runtime.Variable.variable;
import static com.mikosik.stork.model.syntax.BracketType.ROUND;
import static com.mikosik.stork.model.syntax.Visitor.visit;

import java.math.BigInteger;

import com.mikosik.stork.model.def.Definition;
import com.mikosik.stork.model.runtime.Expression;
import com.mikosik.stork.model.syntax.Bracket;
import com.mikosik.stork.model.syntax.Sentence;
import com.mikosik.stork.model.syntax.Syntax;
import com.mikosik.stork.model.syntax.Visitor;
import com.mikosik.stork.model.syntax.Word;

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
        if (tail.parts.isEmpty()) {
          return primitive(new BigInteger(head.string));
        } else {
          throw new RuntimeException("integer cannot be followed by sentence");
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
    check(bracket.sentence.parts.size() == 1);
    Word word = (Word) first(bracket.sentence.parts);
    return word.string;
  }
}
