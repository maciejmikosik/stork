package com.mikosik.stork.build.compile;

import static com.mikosik.stork.build.compile.Parser.parse;
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
import static com.mikosik.stork.problem.build.compile.UnexpectedToken.unexpected;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import com.mikosik.stork.common.Peekerator;
import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Lambda;
import com.mikosik.stork.model.Module;

public class Compiler {
  public static Module compile(Iterator<Byte> input) {
    return compile(peekerator(checkingEOF(parse(input))));
  }

  public static Module compile(Peekerator<Token> input) {
    var definitions = new LinkedList<Definition>();
    while (input.hasNext()) {
      definitions.add(compileDefinition(input));
    }
    return module(definitions);
  }

  private static Definition compileDefinition(Peekerator<Token> input) {
    return definition(
        compileName(input),
        compileBody(input));
  }

  private static Expression compileExpression(Peekerator<Token> input) {
    return switch (input.peek()) {
      case Label label -> compileInvocation(input);
      case Symbol symbol when symbol.character == '(' -> compileLambda(input);
      default -> switch (input.next()) {
        case StringLiteral literal -> quote(literal.string);
        case IntegerLiteral literal -> stork(literal.value);
        case Token token -> failUnexpected(token);
      };
    };
  }

  private static Expression compileInvocation(Peekerator<Token> input) {
    var function = compileVariable(input);
    return compileArguments(function, input);
  }

  private static Expression compileVariable(Peekerator<Token> input) {
    return variable(compileName(input));
  }

  private static Expression compileArguments(
      Expression function,
      Peekerator<Token> input) {
    return switch (input.peek()) {
      case Symbol symbol when symbol.character == '(' -> {
        yield compileSomeArguments(function, input);
      }
      default -> function;
    };
  }

  private static Expression compileSomeArguments(
      Expression function,
      Peekerator<Token> input) {
    var argument = compileArgument(input);
    var application = application(function, argument);
    return compileArguments(application, input);
  }

  private static Expression compileArgument(Peekerator<Token> input) {
    checkNextSymbol('(', input);
    var argument = compileExpression(input);
    checkNextSymbol(')', input);
    return argument;
  }

  private static Lambda compileLambda(Peekerator<Token> input) {
    checkNextSymbol('(', input);
    var parameter = parameter(compileName(input));
    checkNextSymbol(')', input);
    return lambda(parameter, compileBody(input));
  }

  private static Expression compileBody(Peekerator<Token> input) {
    return switch (input.peek()) {
      case Symbol symbol when symbol.character == '(' -> compileLambda(input);
      case Symbol symbol when symbol.character == '{' -> compileScope(input);
      case Token token -> failUnexpected(token);
    };
  }

  private static Expression compileScope(Peekerator<Token> input) {
    checkNextSymbol('{', input);
    var body = compileExpression(input);
    checkNextSymbol('}', input);
    return body;
  }

  private static String compileName(Peekerator<Token> input) {
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
