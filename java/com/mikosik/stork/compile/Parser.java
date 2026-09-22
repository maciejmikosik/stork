package com.mikosik.stork.compile;

import static com.mikosik.stork.common.col.Collections.checkSuchElement;
import static com.mikosik.stork.common.col.ImmutableList.listFrom;
import static com.mikosik.stork.common.col.Peekerator.peekerator;
import static com.mikosik.stork.common.err.Throwables.check;
import static com.mikosik.stork.common.err.Throwables.runtimeException;
import static com.mikosik.stork.compile.Bridge.stork;
import static com.mikosik.stork.compile.err.CompilerException.exception;
import static com.mikosik.stork.compile.err.Problems.unexpectedToken;
import static com.mikosik.stork.model.exp.Application.application;
import static com.mikosik.stork.model.exp.Definition.definition;
import static com.mikosik.stork.model.exp.Identifier.identifier;
import static com.mikosik.stork.model.exp.Lambda.lambda;
import static com.mikosik.stork.model.exp.Parameter.parameter;
import static com.mikosik.stork.model.exp.Quote.quote;
import static com.mikosik.stork.model.exp.Variable.variable;
import static com.mikosik.stork.model.token.Punctuator.DOT;
import static com.mikosik.stork.model.token.Punctuator.LEFT_CURLY_BRACKET;
import static com.mikosik.stork.model.token.Punctuator.LEFT_ROUND_BRACKET;
import static com.mikosik.stork.model.token.Punctuator.RIGHT_CURLY_BRACKET;
import static com.mikosik.stork.model.token.Punctuator.RIGHT_ROUND_BRACKET;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.mikosik.stork.common.col.Peekerator;
import com.mikosik.stork.model.exp.Definition;
import com.mikosik.stork.model.exp.Expression;
import com.mikosik.stork.model.exp.Lambda;
import com.mikosik.stork.model.token.IntegerLiteral;
import com.mikosik.stork.model.token.Label;
import com.mikosik.stork.model.token.Punctuator;
import com.mikosik.stork.model.token.StringLiteral;
import com.mikosik.stork.model.token.Token;

public class Parser {
  public static List<Definition> parse(Iterator<Token> iterator) {
    return parse(peekerator(checkingEOF(iterator)));
  }

  private static List<Definition> parse(Peekerator<Token> input) {
    return listFrom(parseDefinitions(input));
  }

  private static Iterator<Definition> parseDefinitions(Peekerator<Token> input) {
    return new Iterator<>() {
      public boolean hasNext() {
        return input.hasNext();
      }

      public Definition next() {
        checkSuchElement(hasNext());
        return parseDefinition(input);
      }
    };
  }

  private static Definition parseDefinition(Peekerator<Token> input) {
    return definition(
        identifier(variable(parseName(input))),
        parseBody(input));
  }

  private static Expression parseExpression(Peekerator<Token> input) {
    return switch (input.peek()) {
      case DOT -> parsePipe(input);
      default -> parseChain(input);
    };
  }

  private static Expression parsePipe(Peekerator<Token> input) {
    var parameter = parameter("pipeParameter");
    Expression body = parameter;
    while (input.peek() == DOT) {
      input.next();
      body = application(parseUnchainedExpression(input), body);
    }
    return lambda(parameter, body);
  }

  private static Expression parseChain(Peekerator<Token> input) {
    var result = parseUnchainedExpression(input);
    while (input.peek() == DOT) {
      input.next();
      result = application(parseUnchainedExpression(input), result);
    }
    return result;
  }

  private static Expression parseUnchainedExpression(Peekerator<Token> input) {
    return switch (input.peek()) {
      case Label label -> parseInvocation(input);
      case LEFT_ROUND_BRACKET -> parseLambda(input);
      default -> switch (input.next()) {
        case StringLiteral literal -> quote(literal.string);
        case IntegerLiteral literal -> stork(literal.value);
        case Token token -> failUnexpected(token);
      };
    };
  }

  private static Expression parseInvocation(Peekerator<Token> input) {
    var function = parseVariable(input);
    return parseArguments(function, input);
  }

  private static Expression parseVariable(Peekerator<Token> input) {
    return variable(parseName(input));
  }

  private static Expression parseArguments(
      Expression function,
      Peekerator<Token> input) {
    return switch (input.peek()) {
      case LEFT_ROUND_BRACKET -> parseSomeArguments(function, input);
      default -> function;
    };
  }

  private static Expression parseSomeArguments(
      Expression function,
      Peekerator<Token> input) {
    var argument = parseArgument(input);
    var application = application(function, argument);
    return parseArguments(application, input);
  }

  private static Expression parseArgument(Peekerator<Token> input) {
    checkNext(LEFT_ROUND_BRACKET, input);
    var argument = parseExpression(input);
    checkNext(RIGHT_ROUND_BRACKET, input);
    return argument;
  }

  private static Lambda parseLambda(Peekerator<Token> input) {
    checkNext(LEFT_ROUND_BRACKET, input);
    var parameter = parameter(parseName(input));
    checkNext(RIGHT_ROUND_BRACKET, input);
    return lambda(parameter, parseBody(input));
  }

  private static Expression parseBody(Peekerator<Token> input) {
    return switch (input.peek()) {
      case LEFT_ROUND_BRACKET -> parseLambda(input);
      case LEFT_CURLY_BRACKET -> parseScope(input);
      case Token token -> failUnexpected(token);
    };
  }

  private static Expression parseScope(Peekerator<Token> input) {
    checkNext(LEFT_CURLY_BRACKET, input);
    var body = parseExpression(input);
    checkNext(RIGHT_CURLY_BRACKET, input);
    return body;
  }

  private static String parseName(Peekerator<Token> input) {
    return next(Label.class, input).string;
  }

  private static void checkNext(Punctuator punctuator, Peekerator<Token> input) {
    check(next(Punctuator.class, input) == punctuator);
  }

  private static <T extends Token> T next(Class<T> type, Peekerator<Token> input) {
    Token token = input.next();
    return type.isInstance(token)
        ? type.cast(token)
        : failUnexpected(token);
  }

  private static <T> T failUnexpected(Token token) {
    throw exception(unexpectedToken()
        .object(token)
        .build());
  }

  private static <E> Iterator<E> checkingEOF(Iterator<E> iterator) {
    return new Iterator<E>() {
      public boolean hasNext() {
        return iterator.hasNext();
      }

      public E next() {
        // TODO throw dedicated problem or at least use check(hasNext())
        try {
          return iterator.next();
        } catch (NoSuchElementException e) {
          throw runtimeException("unexpected end of file");
        }
      }
    };
  }
}
