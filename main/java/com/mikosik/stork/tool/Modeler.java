package com.mikosik.stork.tool;

import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.data.model.Application.application;
import static com.mikosik.stork.data.model.Definition.definition;
import static com.mikosik.stork.data.model.Lambda.lambda;
import static com.mikosik.stork.data.model.Parameter.parameter;
import static com.mikosik.stork.data.model.Primitive.primitive;
import static com.mikosik.stork.data.model.Variable.variable;
import static com.mikosik.stork.data.syntax.BracketType.ROUND;

import java.math.BigInteger;

import com.mikosik.stork.data.model.Application;
import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.ExpressionVisitor;
import com.mikosik.stork.data.model.Lambda;
import com.mikosik.stork.data.model.Parameter;
import com.mikosik.stork.data.model.Variable;
import com.mikosik.stork.data.syntax.Bracket;
import com.mikosik.stork.data.syntax.Sentence;
import com.mikosik.stork.data.syntax.SentenceVisitor;
import com.mikosik.stork.data.syntax.Syntax;
import com.mikosik.stork.data.syntax.Word;

public class Modeler {
  public static Definition modelDefinition(Sentence sentence) {
    SentenceVisitor<Definition> visitor = new SentenceVisitor<Definition>() {
      protected Definition visitLabel(Word head, Sentence tail) {
        return definition(head.string, modelLambda(tail));
      }
    };
    return visitor.visit(sentence);
  }

  public static Expression modelExpression(Sentence sentence) {
    SentenceVisitor<Expression> visitor = new SentenceVisitor<Expression>() {
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
    };
    return visitor.visit(sentence);
  }

  private static Expression modelApplication(Sentence sentence) {
    SentenceVisitor<Expression> visitor = new SentenceVisitor<Expression>() {
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
    };
    return visitor.visit(sentence);
  }

  private static Expression modelLambda(Sentence sentence) {
    SentenceVisitor<Expression> visitor = new SentenceVisitor<Expression>() {
      protected Expression visitRound(Bracket head, Sentence tail) {
        Parameter parameter = parameterIn(head);
        Expression body = bind(parameter, modelLambda(tail));
        return lambda(parameter, body);
      }

      protected Expression visitCurly(Bracket head, Sentence tail) {
        // TODO rest of sentence after bracket is ignored right now
        return modelExpression(head.sentence);
      }
    };
    return visitor.visit(sentence);
  }

  private static Parameter parameterIn(Bracket bracket) {
    check(bracket.type == ROUND);
    check(!bracket.sentence.parts.tail().available());
    Word word = (Word) bracket.sentence.parts.head();
    return parameter(word.string);
  }

  private static Expression bind(Parameter parameter, Expression expression) {
    ExpressionVisitor<Expression> visitor = new ExpressionVisitor<Expression>() {
      protected Expression visit(Variable variable) {
        return variable.name.equals(parameter.name)
            ? parameter
            : variable;
      }

      protected Expression visit(Application application) {
        return application(
            bind(parameter, application.function),
            bind(parameter, application.argument));
      }

      protected Expression visit(Lambda lambda) {
        // TODO test shadowing
        return lambda.parameter.name.equals(parameter.name)
            ? lambda
            : lambda(
                lambda.parameter,
                bind(parameter, lambda.body));
      }

      protected Expression visit(Parameter parameter) {
        return parameter;
      }
    };
    return visitor.visit(expression);
  }
}
