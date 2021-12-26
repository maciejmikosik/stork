package com.mikosik.stork.tool.compile;

import static com.mikosik.stork.common.Chain.empty;
import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.common.Logic.not;
import static com.mikosik.stork.common.Throwables.fail;
import static com.mikosik.stork.common.io.Ascii.isAlphanumeric;
import static com.mikosik.stork.common.io.Ascii.isDoubleQuote;
import static com.mikosik.stork.common.io.Ascii.isLetter;
import static com.mikosik.stork.common.io.Ascii.isNumeric;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.Integer.integer;
import static com.mikosik.stork.model.Lambda.lambda;
import static com.mikosik.stork.model.Module.module;
import static com.mikosik.stork.model.Parameter.parameter;
import static com.mikosik.stork.model.Quote.quote;
import static com.mikosik.stork.model.Variable.variable;
import static java.nio.charset.StandardCharsets.US_ASCII;

import java.math.BigInteger;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.common.io.Ascii;
import com.mikosik.stork.common.io.Blob;
import com.mikosik.stork.common.io.Input;
import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Lambda;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Parameter;
import com.mikosik.stork.model.Variable;
import com.mikosik.stork.tool.common.Traverser;

public class Compiler {
  public Module compileModule(Input input) {
    Chain<Definition> definitions = empty();
    while (input.peek() != -1) {
      skipWhitespaces(input);
      definitions = definitions.add(compileDefinition(input));
      skipWhitespaces(input);
    }
    return module(definitions.reverse());
  }

  public Definition compileDefinition(Input input) {
    Variable name = compileVariable(input);
    skipWhitespaces(input);
    return definition(name, compileBody(input));
  }

  public Expression compileExpression(Input input) {
    int character = input.peek();
    if (isNumeric(character)) {
      return compileInteger(input);
    } else if (isLetter(character)) {
      return compileInvocation(input);
    } else if (isDoubleQuote(character)) {
      return compileQuote(input);
    } else if (character == '(') {
      return compileLambda(input);
    } else {
      return fail("unexpected character %c", character);
    }
  }

  protected Expression compileInteger(Input input) {
    return integer(new BigInteger(compileAlphanumeric(input)));
  }

  protected Expression compileInvocation(Input input) {
    Expression result = compileVariable(input);
    skipWhitespaces(input);
    while (input.peek() == '(') {
      input.read();
      skipWhitespaces(input);
      Expression argument = compileExpression(input);
      skipWhitespaces(input);
      check(input.read() == ')');
      result = application(result, argument);
      skipWhitespaces(input);
    }
    return result;
  }

  protected Variable compileVariable(Input input) {
    return variable(compileAlphanumeric(input));
  }

  protected Expression compileQuote(Input input) {
    check(isDoubleQuote(input.read()));
    Blob blob = input.readAllBytes(not(Ascii::isDoubleQuote));
    check(isDoubleQuote(input.read()));
    return quote(asciiString(blob));
  }

  protected Lambda compileLambda(Input input) {
    check(input.read() == '(');
    skipWhitespaces(input);
    Parameter parameter = parameter(compileAlphanumeric(input));
    skipWhitespaces(input);
    check(input.read() == ')');
    skipWhitespaces(input);
    return lambda(parameter, bind(parameter, compileBody(input)));
  }

  protected Expression bind(Parameter parameter, Expression expression) {
    return new Traverser() {
      protected Expression traverse(Variable variable) {
        return variable.name.equals(parameter.name)
            ? parameter
            : variable;
      }

      protected Expression traverse(Lambda lambda) {
        return lambda.parameter.name.equals(parameter.name)
            ? lambda // TODO test shadowing
            : super.traverse(lambda);
      }
    }.traverse(expression);
  }

  protected Expression compileBody(Input input) {
    int character = input.peek();
    if (character == '(') {
      return compileLambda(input);
    } else if (character == '{') {
      return compileScope(input);
    } else {
      return fail("expected ( or { but was %c", character);
    }
  }

  protected Expression compileScope(Input input) {
    check(input.read() == '{');
    skipWhitespaces(input);
    Expression body = compileExpression(input);
    skipWhitespaces(input);
    check(input.read() == '}');
    return body;
  }

  protected String compileAlphanumeric(Input input) {
    return isAlphanumeric(input.peek())
        ? asciiString(input.readAllBytes(Ascii::isAlphanumeric))
        : fail("expected alphanumeric but was %c", input.peek());
  }

  protected void skipWhitespaces(Input input) {
    input.readAllBytes(Ascii::isWhitespace);
  }

  private static String asciiString(Blob blob) {
    return new String(blob.bytes, US_ASCII);
  }
}
