package com.mikosik.stork.build.parse;

import static com.mikosik.stork.build.link.Bridge.stork;
import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.common.Peekerator.peekerator;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.Lambda.lambda;
import static com.mikosik.stork.model.Module.module;
import static com.mikosik.stork.model.Parameter.parameter;
import static com.mikosik.stork.model.Quote.quote;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.problem.ProblemException.exception;
import static com.mikosik.stork.problem.build.parse.UnexpectedToken.unexpected;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import com.mikosik.stork.build.tokenize.IntegerLiteral;
import com.mikosik.stork.build.tokenize.Label;
import com.mikosik.stork.build.tokenize.StringLiteral;
import com.mikosik.stork.build.tokenize.Symbol;
import com.mikosik.stork.build.tokenize.Token;
import com.mikosik.stork.common.Peekerator;
import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Lambda;
import com.mikosik.stork.model.Module;

public class Parser {
  public static Module parse(Iterator<Token> iterator) {
    return parse(peekerator(checkingEOF(iterator)));
  }

  private static Module parse(Peekerator<Token> input) {
    var definitions = new LinkedList<Definition>();
    while (input.hasNext()) {
      definitions.add(parseDefinition(input));
    }
    return module(definitions);
  }

  private static Definition parseDefinition(Peekerator<Token> input) {
    return definition(
        parseName(input),
        parseBody(input));
  }

  private static Expression parseExpression(Peekerator<Token> input) {
    return switch (input.peek()) {
      case Label label -> parseInvocation(input);
      case Symbol symbol -> switch (symbol.character) {
        case '(' -> parseLambda(input);
        default -> failUnexpected(symbol);
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
      case Symbol symbol -> switch (symbol.character) {
        case '(' -> parseSomeArguments(function, input);
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
    checkNextSymbol('(', input);
    var argument = parseExpression(input);
    checkNextSymbol(')', input);
    return argument;
  }

  private static Lambda parseLambda(Peekerator<Token> input) {
    checkNextSymbol('(', input);
    var parameter = parameter(parseName(input));
    checkNextSymbol(')', input);
    return lambda(parameter, parseBody(input));
  }

  private static Expression parseBody(Peekerator<Token> input) {
    return switch (input.peek()) {
      case Symbol symbol -> switch (symbol.character) {
        case '(' -> parseLambda(input);
        case '{' -> parseScope(input);
        default -> failUnexpected(symbol);
      };
      case Token token -> failUnexpected(token);
    };
  }

  private static Expression parseScope(Peekerator<Token> input) {
    checkNextSymbol('{', input);
    var body = parseExpression(input);
    checkNextSymbol('}', input);
    return body;
  }

  private static String parseName(Peekerator<Token> input) {
    return next(Label.class, input).string;
  }

  private static void checkNextSymbol(char character, Peekerator<Token> input) {
    check(next(Symbol.class, input).character == character);
  }

  private static <T extends Token> T next(Class<T> type, Peekerator<Token> input) {
    Token token = input.next();
    return type.isInstance(token)
        ? (T) token
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
          throw new RuntimeException("unexpected end of file");
        }
      }
    };
  }
}
