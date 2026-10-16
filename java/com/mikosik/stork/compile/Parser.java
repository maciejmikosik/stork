package com.mikosik.stork.compile;

import static com.mikosik.stork.common.col.Collections.checkSuchElement;
import static com.mikosik.stork.common.col.Peekerator.peekerator;
import static com.mikosik.stork.common.err.Throwables.runtimeException;
import static com.mikosik.stork.compile.Bridge.stork;
import static com.mikosik.stork.compile.err.CompilerException.exception;
import static com.mikosik.stork.compile.err.Context.in;
import static com.mikosik.stork.compile.err.Problems.unexpectedToken;
import static com.mikosik.stork.model.exp.Application.application;
import static com.mikosik.stork.model.exp.Definition.definition;
import static com.mikosik.stork.model.exp.Lambda.lambda;
import static com.mikosik.stork.model.exp.Parameter.parameter;
import static com.mikosik.stork.model.exp.Quote.quote;
import static com.mikosik.stork.model.exp.Reference.reference;
import static com.mikosik.stork.model.exp.Variable.variable;
import static com.mikosik.stork.model.token.Punctuator.DOT;
import static com.mikosik.stork.model.token.Punctuator.LEFT_CURLY_BRACKET;
import static com.mikosik.stork.model.token.Punctuator.LEFT_ROUND_BRACKET;
import static com.mikosik.stork.model.token.Punctuator.RIGHT_CURLY_BRACKET;
import static com.mikosik.stork.model.token.Punctuator.RIGHT_ROUND_BRACKET;
import static java.util.Objects.deepEquals;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.mikosik.stork.common.col.Peekerator;
import com.mikosik.stork.model.exp.Definition;
import com.mikosik.stork.model.exp.Expression;
import com.mikosik.stork.model.exp.Lambda;
import com.mikosik.stork.model.token.Identifier;
import com.mikosik.stork.model.token.IntegerLiteral;
import com.mikosik.stork.model.token.StringLiteral;
import com.mikosik.stork.model.token.Token;

public class Parser implements Iterator<Definition> {
  private final Peekerator<Token> input;

  private Parser(Peekerator<Token> input) {
    this.input = input;
  }

  public static Iterator<Definition> parse(Iterator<Token> input) {
    return new Parser(peekerator(checkingEOF(input)));
  }

  public boolean hasNext() {
    return input.hasNext();
  }

  public Definition next() {
    checkSuchElement(hasNext());
    return parseDefinition();
  }

  private Definition parseDefinition() {
    var name = variable(parseName());
    return definition(
        reference(name),
        in(name).run(this::parseBody));
  }

  private Expression parseExpression() {
    return switch (input.peek()) {
      case DOT -> parsePipe();
      default -> parseChain();
    };
  }

  private Expression parsePipe() {
    var parameter = parameter("synthetic_pipe_parameter");
    Expression body = parameter;
    while (input.peek() == DOT) {
      input.next();
      body = application(parseUnchainedExpression(), body);
    }
    return lambda(parameter, body);
  }

  private Expression parseChain() {
    var result = parseUnchainedExpression();
    while (input.peek() == DOT) {
      input.next();
      result = application(parseUnchainedExpression(), result);
    }
    return result;
  }

  private Expression parseUnchainedExpression() {
    return switch (input.peek()) {
      case Identifier identifier -> parseInvocation();
      case LEFT_ROUND_BRACKET -> parseLambda();
      default -> switch (input.next()) {
        case StringLiteral literal -> quote(literal.string);
        case IntegerLiteral literal -> stork(literal.value);
        case Token token -> failUnexpected(token);
      };
    };
  }

  private Expression parseInvocation() {
    var invocation = parseVariable();
    while (input.peek() == LEFT_ROUND_BRACKET) {
      invocation = application(invocation, parseArgument());
    }
    return invocation;
  }

  private Expression parseVariable() {
    return variable(parseName());
  }

  private Expression parseArgument() {
    checkNext(LEFT_ROUND_BRACKET);
    var argument = parseExpression();
    checkNext(RIGHT_ROUND_BRACKET);
    return argument;
  }

  private Lambda parseLambda() {
    checkNext(LEFT_ROUND_BRACKET);
    var parameter = parameter(parseName());
    checkNext(RIGHT_ROUND_BRACKET);
    return lambda(parameter, parseBody());
  }

  private Expression parseBody() {
    return switch (input.peek()) {
      case LEFT_ROUND_BRACKET -> parseLambda();
      case LEFT_CURLY_BRACKET -> parseScope();
      case Token token -> failUnexpected(token);
    };
  }

  private Expression parseScope() {
    checkNext(LEFT_CURLY_BRACKET);
    var body = parseExpression();
    checkNext(RIGHT_CURLY_BRACKET);
    return body;
  }

  private String parseName() {
    return next(Identifier.class).string;
  }

  private void checkNext(Token expected) {
    var actual = input.next();
    if (!deepEquals(actual, expected)) {
      throw exception(unexpectedToken()
          .object(actual)
          .build());
    }
  }

  private <T extends Token> T next(Class<T> type) {
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
        try {
          return iterator.next();
        } catch (NoSuchElementException e) {
          throw runtimeException("unexpected end of file");
        }
      }
    };
  }
}
