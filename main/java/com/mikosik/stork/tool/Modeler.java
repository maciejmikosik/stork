package com.mikosik.stork.tool;

import static com.mikosik.stork.data.model.Application.application;
import static com.mikosik.stork.data.model.Definition.definition;
import static com.mikosik.stork.data.model.Lambda.lambda;
import static com.mikosik.stork.data.model.Noun.noun;
import static com.mikosik.stork.data.model.Parameter.parameter;
import static com.mikosik.stork.data.model.Switch.switchOn;
import static com.mikosik.stork.data.model.Variable.variable;
import static com.mikosik.stork.data.syntax.Switch.switchOn;
import static com.mikosik.stork.tool.common.Translate.asStorkStream;
import static java.lang.String.format;

import java.math.BigInteger;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Parameter;
import com.mikosik.stork.data.syntax.Syntax;

public class Modeler {
  public static Definition modelDefinition(Chain<Syntax> sentence) {
    return switchOn(sentence)
        .ifName((alphanumeric, tail) -> definition(alphanumeric.string, modelLambda(tail)))
        .elseFail();
  }

  public static Expression modelExpression(Chain<Syntax> sentence) {
    return switchOn(sentence)
        .ifName((alphanumeric, tail) -> modelApplication(sentence))
        .ifInteger((alphanumeric, tail) -> modelInteger(sentence))
        .ifQuote((quote, tail) -> modelString(sentence))
        .ifRoundBracket((bracket, tail) -> modelLambda(sentence))
        .elseFail();
  }

  private static Expression modelString(Chain<Syntax> sentence) {
    return switchOn(sentence)
        .ifSentence(s -> fail("double quoted text cannot be followed by sentence"))
        .ifQuote((quote, tail) -> asStorkStream(quote.ascii))
        .elseFail();
  }

  private static Expression modelInteger(Chain<Syntax> sentence) {
    return switchOn(sentence)
        // TODO test integer followed by sentence
        .ifSentence(s -> fail("integer cannot be followed by sentence"))
        .ifInteger((alphanumeric, tail) -> noun(new BigInteger(alphanumeric.string)))
        .elseFail();
  }

  private static Expression modelApplication(Chain<Syntax> sentence) {
    return switchOn(sentence)
        .ifName((alphanumeric, tail) -> modelApplication(variable(alphanumeric.string), tail))
        .elseFail();
  }

  private static Expression modelApplication(Expression function, Chain<Syntax> sentence) {
    return switchOn(sentence)
        .ifEmpty(function)
        .ifRoundBracket((bracket, tail) -> modelApplication(
            application(function, modelExpression(bracket.sentence)),
            tail))
        .elseFail();
  }

  private static Expression modelLambda(Chain<Syntax> sentence) {
    return switchOn(sentence)
        .ifRoundBracket((bracket, tail) -> {
          Parameter parameter = modelParameter(bracket.sentence);
          Expression body = bind(parameter, modelLambda(tail));
          return lambda(parameter, body);
        })
        // TODO rest of sentence after bracket is ignored right now
        .ifCurlyBracket((bracket, tail) -> modelExpression(bracket.sentence))
        .elseFail();
  }

  private static Parameter modelParameter(Chain<Syntax> sentence) {
    return switchOn(sentence)
        .ifSentence(s -> fail(format("parameter must be single word, not '%s'", sentence)))
        .ifName((alphanumeric, tail) -> parameter(alphanumeric.string))
        .elseFail();
  }

  private static Expression bind(Parameter parameter, Expression expression) {
    return switchOn(expression)
        .ifVariable(variable -> variable.name.equals(parameter.name)
            ? parameter
            : variable)
        .ifNoun(noun -> noun)
        .ifApplication(application -> application(
            bind(parameter, application.function),
            bind(parameter, application.argument)))
        .ifLambda(lambda -> lambda.parameter.name.equals(parameter.name)
            ? lambda // TODO test shadowing
            : lambda(
                lambda.parameter,
                bind(parameter, lambda.body)))
        .ifParameter(param -> param)
        .elseFail();
  }

  private static <X> X fail(String message) {
    throw new RuntimeException(message);
  }
}
