package com.mikosik.stork.tool.compile;

import static com.mikosik.stork.common.Ascii.DOUBLE_QUOTE;
import static com.mikosik.stork.common.Ascii.isAlphanumeric;
import static com.mikosik.stork.common.Ascii.isLetter;
import static com.mikosik.stork.common.Ascii.isNumeric;
import static com.mikosik.stork.common.Ascii.isWhitespace;
import static com.mikosik.stork.common.Chain.empty;
import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.common.PeekingInput.peeking;
import static com.mikosik.stork.common.Throwables.fail;
import static com.mikosik.stork.data.model.Application.application;
import static com.mikosik.stork.data.model.Definition.definition;
import static com.mikosik.stork.data.model.Integer.integer;
import static com.mikosik.stork.data.model.Lambda.lambda;
import static com.mikosik.stork.data.model.Module.module;
import static com.mikosik.stork.data.model.Parameter.parameter;
import static com.mikosik.stork.data.model.Variable.variable;
import static com.mikosik.stork.tool.common.Translate.asStorkStream;
import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.common.Input;
import com.mikosik.stork.common.PeekingInput;
import com.mikosik.stork.data.model.Application;
import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Lambda;
import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.data.model.Parameter;
import com.mikosik.stork.data.model.Variable;

public class Compiler {
  private Compiler() {}

  public static Compiler compiler() {
    return new Compiler();
  }

  public Module compile(Input input) {
    return compileModule(peeking(input));
  }

  public static Module compileModule(PeekingInput input) {
    Chain<Definition> definitions = empty();
    while (input.peek() != -1) {
      skipWhitespaces(input);
      definitions = definitions.add(compileDefinition(input));
      skipWhitespaces(input);
    }
    return module(definitions.reverse());
  }

  private static Definition compileDefinition(PeekingInput input) {
    Variable variable = compileVariable(input);
    skipWhitespaces(input);
    Expression expression = compileLambdaOrScope(input);
    return definition(variable, expression);
  }

  private static Variable compileVariable(PeekingInput input) {
    return isAlphanumeric(input.peek())
        ? variable(string(parseAlphanumeric(input)))
        : fail("expected alphanumeric but was %c", input.peek());
  }

  private static Expression compileLambdaOrScope(PeekingInput input) {
    int character = input.peek();
    if (character == '(') {
      return compileLambda(input);
    } else if (character == '{') {
      return compileScope(input);
    } else {
      return fail("expected ( or { but was %c", character);
    }
  }

  private static Expression compileLambda(PeekingInput input) {
    check(input.read() == '(');
    skipWhitespaces(input);
    Parameter parameter = parameter(string(parseAlphanumeric(input)));
    skipWhitespaces(input);
    check(input.read() == ')');
    skipWhitespaces(input);
    Expression body = bind(parameter, compileLambdaOrScope(input));
    return lambda(parameter, body);
  }

  private static Expression bind(Parameter parameter, Expression expression) {
    if (expression instanceof Variable) {
      return bind(parameter, (Variable) expression);
    } else if (expression instanceof Application) {
      return bind(parameter, (Application) expression);
    } else if (expression instanceof Lambda) {
      return bind(parameter, (Lambda) expression);
    } else {
      return expression;
    }
  }

  private static Expression bind(Parameter parameter, Variable variable) {
    return variable.name.equals(parameter.name)
        ? parameter
        : variable;
  }

  private static Expression bind(Parameter parameter, Application application) {
    return application(
        bind(parameter, application.function),
        bind(parameter, application.argument));
  }

  // TODO test shadowing
  private static Expression bind(Parameter parameter, Lambda lambda) {
    return lambda.parameter.name.equals(parameter.name)
        ? lambda
        : lambda(
            lambda.parameter,
            bind(parameter, lambda.body));
  }

  private static Expression compileScope(PeekingInput input) {
    check(input.read() == '{');
    skipWhitespaces(input);
    Expression expression = compileExpression(input);
    skipWhitespaces(input);
    check(input.read() == '}');
    return expression;
  }

  private static Expression compileExpression(PeekingInput input) {
    int character = input.peek();
    if (isNumeric(character)) {
      return compileInteger(input);
    } else if (isLetter(character)) {
      return compileApplicationOrVariable(input);
    } else if (character == DOUBLE_QUOTE) {
      return compileString(input);
    } else if (character == '(') {
      return compileLambda(input);
    } else {
      return fail("unexpected character %c", character);
    }
  }

  private static Expression compileString(PeekingInput input) {
    return asStorkStream(string(parseQuote(input)));
  }

  private static Expression compileInteger(PeekingInput input) {
    return integer(new BigInteger(string(parseAlphanumeric(input))));
  }

  private static Expression compileApplicationOrVariable(PeekingInput input) {
    Expression expression = variable(string(parseAlphanumeric(input)));
    skipWhitespaces(input);
    while (input.peek() == '(') {
      input.read();
      skipWhitespaces(input);
      Expression argument = compileExpression(input);
      skipWhitespaces(input);
      check(input.read() == ')');
      expression = application(expression, argument);
      skipWhitespaces(input);
    }
    return expression;
  }

  private static void skipWhitespaces(PeekingInput input) {
    while (input.peek() != -1 && isWhitespace(input.peek())) {
      input.read();
    }
  }

  private static byte[] parseAlphanumeric(PeekingInput input) {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    while (isAlphanumeric(input.peek())) {
      buffer.write(input.read());
    }
    return buffer.toByteArray();
  }

  private static byte[] parseQuote(PeekingInput input) {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    check(input.read() == DOUBLE_QUOTE);
    while (input.peek() != -1 && input.peek() != DOUBLE_QUOTE) {
      buffer.write(input.read());
    }
    check(input.read() == DOUBLE_QUOTE);
    return buffer.toByteArray();
  }

  private static String string(byte[] buffer) {
    return new String(buffer, US_ASCII);
  }
}
