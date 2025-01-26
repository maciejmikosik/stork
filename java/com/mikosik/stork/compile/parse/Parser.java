package com.mikosik.stork.compile.parse;

import static com.mikosik.stork.common.Collections.checkSuchElement;
import static com.mikosik.stork.common.Peekerator.peekerator;
import static com.mikosik.stork.common.Sequence.sequenceFrom;
import static com.mikosik.stork.common.Throwables.check;
import static com.mikosik.stork.common.Throwables.runtimeException;
import static com.mikosik.stork.compile.link.Bridge.stork;
import static com.mikosik.stork.compile.tokenize.Bracket.RIGHT_CURLY_BRACKET;
import static com.mikosik.stork.compile.tokenize.Bracket.RIGHT_ROUND_BRACKET;
import static com.mikosik.stork.compile.tokenize.Bracket.LEFT_CURLY_BRACKET;
import static com.mikosik.stork.compile.tokenize.Bracket.LEFT_ROUND_BRACKET;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.Lambda.lambda;
import static com.mikosik.stork.model.Library.library;
import static com.mikosik.stork.model.Parameter.parameter;
import static com.mikosik.stork.model.Quote.quote;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.problem.ProblemException.exception;
import static com.mikosik.stork.problem.compile.parse.UnexpectedToken.unexpected;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.mikosik.stork.common.Peekerator;
import com.mikosik.stork.compile.tokenize.IntegerLiteral;
import com.mikosik.stork.compile.tokenize.Label;
import com.mikosik.stork.compile.tokenize.StringLiteral;
import com.mikosik.stork.compile.tokenize.Bracket;
import com.mikosik.stork.compile.tokenize.Token;
import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Lambda;
import com.mikosik.stork.model.Library;

public class Parser {
  public static Library parse(Iterator<Token> iterator) {
    return parse(peekerator(checkingEOF(iterator)));
  }

  private static Library parse(Peekerator<Token> input) {
    return library(sequenceFrom(parseDefinitions(input)));
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
        parseName(input),
        parseBody(input));
  }

  private static Expression parseExpression(Peekerator<Token> input) {
    return switch (input.peek()) {
      case Label label -> parseInvocation(input);
      case Bracket bracket -> switch (bracket) {
        case LEFT_ROUND_BRACKET -> parseLambda(input);
        default -> failUnexpected(bracket);
      };
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
      case Bracket bracket -> switch (bracket) {
        case LEFT_ROUND_BRACKET -> parseSomeArguments(function, input);
        default -> function;
      };
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
    checkNextBracket(LEFT_ROUND_BRACKET, input);
    var argument = parseExpression(input);
    checkNextBracket(RIGHT_ROUND_BRACKET, input);
    return argument;
  }

  private static Lambda parseLambda(Peekerator<Token> input) {
    checkNextBracket(LEFT_ROUND_BRACKET, input);
    var parameter = parameter(parseName(input));
    checkNextBracket(RIGHT_ROUND_BRACKET, input);
    return lambda(parameter, parseBody(input));
  }

  private static Expression parseBody(Peekerator<Token> input) {
    return switch (input.peek()) {
      case Bracket bracket -> switch (bracket) {
        case LEFT_ROUND_BRACKET -> parseLambda(input);
        case LEFT_CURLY_BRACKET -> parseScope(input);
        default -> failUnexpected(bracket);
      };
      case Token token -> failUnexpected(token);
    };
  }

  private static Expression parseScope(Peekerator<Token> input) {
    checkNextBracket(LEFT_CURLY_BRACKET, input);
    var body = parseExpression(input);
    checkNextBracket(RIGHT_CURLY_BRACKET, input);
    return body;
  }

  private static String parseName(Peekerator<Token> input) {
    return next(Label.class, input).string;
  }

  private static void checkNextBracket(Bracket bracket, Peekerator<Token> input) {
    check(next(Bracket.class, input) == bracket);
  }

  private static <T extends Token> T next(Class<T> type, Peekerator<Token> input) {
    Token token = input.next();
    return type.isInstance(token)
        ? type.cast(token)
        : failUnexpected(token);
  }

  private static <T> T failUnexpected(Token token) {
    throw exception(unexpected(token));
  }

  private static <E> Iterator<E> checkingEOF(Iterator<E> iterator) {
    return new Iterator<E>() {
      public boolean hasNext() {
        return iterator.hasNext();
      }

      public E next() {
        try {
          return iterator.next();
        } catch (NoSuchElementException e) {
          throw runtimeException("unexpected end of file");
        }
      }
    };
  }
}
