package com.mikosik.stork.tool;

import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.data.model.Application.application;
import static com.mikosik.stork.data.model.Definition.definition;
import static com.mikosik.stork.data.model.ExpressionSwitcher.expressionSwitcherReturning;
import static com.mikosik.stork.data.model.Lambda.lambda;
import static com.mikosik.stork.data.model.Parameter.parameter;
import static com.mikosik.stork.data.model.Primitive.primitive;
import static com.mikosik.stork.data.model.Variable.variable;
import static com.mikosik.stork.data.syntax.BracketType.ROUND;
import static com.mikosik.stork.data.syntax.SentenceSwitcher.sentenceSwitcherReturning;

import java.math.BigInteger;

import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Parameter;
import com.mikosik.stork.data.syntax.Bracket;
import com.mikosik.stork.data.syntax.Sentence;
import com.mikosik.stork.data.syntax.Word;

public class Modeler {
  public static Definition modelDefinition(Sentence sentence) {
    return sentenceSwitcherReturning(Definition.class)
        .ifLabel((word, tail) -> definition(word.string, modelLambda(tail)))
        .apply(sentence);
  }

  public static Expression modelExpression(Sentence sentence) {
    return sentenceSwitcherReturning(Expression.class)
        .ifLabel((word, tail) -> modelApplication(sentence))
        .ifInteger((word, tail) -> {
          if (tail.parts.available()) {
            throw new RuntimeException("integer cannot be followed by sentence");
          } else {
            return primitive(new BigInteger(word.string));
          }
        })
        .ifRoundBracket((bracket, tail) -> modelLambda(sentence))
        .apply(sentence);
  }

  private static Expression modelApplication(Sentence sentence) {
    return sentenceSwitcherReturning(Expression.class)
        .ifLabel((word, tail) -> modelApplication(variable(word.string), tail))
        .apply(sentence);
  }

  private static Expression modelApplication(Expression function, Sentence sentence) {
    return sentenceSwitcherReturning(Expression.class)
        .ifEmpty(() -> function)
        .ifRoundBracket((bracket, tail) -> modelApplication(
            application(function, modelExpression(bracket.sentence)),
            tail))
        .apply(sentence);
  }

  private static Expression modelLambda(Sentence sentence) {
    return sentenceSwitcherReturning(Expression.class)
        .ifRoundBracket((bracket, tail) -> {
          Parameter parameter = parameterIn(bracket);
          Expression body = bind(parameter, modelLambda(tail));
          return lambda(parameter, body);
        })
        // TODO rest of sentence after bracket is ignored right now
        .ifCurlyBracket((bracket, tail) -> modelExpression(bracket.sentence))
        .apply(sentence);
  }

  private static Parameter parameterIn(Bracket bracket) {
    check(bracket.type == ROUND);
    check(!bracket.sentence.parts.tail().available());
    Word word = (Word) bracket.sentence.parts.head();
    return parameter(word.string);
  }

  private static Expression bind(Parameter parameter, Expression expression) {
    return expressionSwitcherReturning(Expression.class)
        .ifVariable(variable -> variable.name.equals(parameter.name)
            ? parameter
            : variable)
        .ifApplication(application -> application(
            bind(parameter, application.function),
            bind(parameter, application.argument)))
        .ifLambda(lambda -> lambda.parameter.name.equals(parameter.name)
            ? lambda // TODO test shadowing
            : lambda(
                lambda.parameter,
                bind(parameter, lambda.body)))
        .ifParameter(param -> param)
        .apply(expression);
  }
}
