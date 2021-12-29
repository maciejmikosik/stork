package com.mikosik.stork.tool.compile;

import static com.mikosik.stork.common.Chain.empty;
import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.common.Logic.not;
import static com.mikosik.stork.common.Throwables.fail;
import static com.mikosik.stork.common.io.Ascii.DOUBLE_QUOTE;
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
import com.mikosik.stork.common.io.MaybeByte;
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
    while (input.peek().hasByte()) {
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
    MaybeByte maybeByte = input.peek();
    if (!maybeByte.hasByte()) {
      return fail("unexpected end of stream", maybeByte);
    } else if (isNumeric(maybeByte.getByte())) {
      return compileInteger(input);
    } else if (isLetter(maybeByte.getByte())) {
      return compileInvocation(input);
    } else if (isDoubleQuote(maybeByte.getByte())) {
      return compileQuote(input);
    } else if (maybeByte.getByte() == '(') {
      return compileLambda(input);
    } else {
      return fail("unexpected character %c", maybeByte);
    }
  }

  protected Expression compileInteger(Input input) {
    return integer(new BigInteger(compileAlphanumeric(input)));
  }

  protected Expression compileInvocation(Input input) {
    Expression result = compileVariable(input);
    skipWhitespaces(input);
    while (input.peek().getByte() == '(') {
      input.read();
      skipWhitespaces(input);
      Expression argument = compileExpression(input);
      skipWhitespaces(input);
      check(input.read().getByte() == ')');
      result = application(result, argument);
      skipWhitespaces(input);
    }
    return result;
  }

  protected Variable compileVariable(Input input) {
    return variable(compileAlphanumeric(input));
  }

  protected Expression compileQuote(Input input) {
    check(input.read().getByte() == DOUBLE_QUOTE);
    Blob blob = input.readAllBytes(not(Ascii::isDoubleQuote));
    check(input.read().getByte() == DOUBLE_QUOTE);
    return quote(asciiString(blob));
  }

  protected Lambda compileLambda(Input input) {
    check(input.read().getByte() == '(');
    skipWhitespaces(input);
    Parameter parameter = parameter(compileAlphanumeric(input));
    skipWhitespaces(input);
    check(input.read().getByte() == ')');
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
    MaybeByte maybeByte = input.peek();
    if (maybeByte.getByte() == '(') {
      return compileLambda(input);
    } else if (maybeByte.getByte() == '{') {
      return compileScope(input);
    } else {
      return fail("expected ( or { but was %c", maybeByte);
    }
  }

  protected Expression compileScope(Input input) {
    check(input.read().getByte() == '{');
    skipWhitespaces(input);
    Expression body = compileExpression(input);
    skipWhitespaces(input);
    check(input.read().getByte() == '}');
    return body;
  }

  protected String compileAlphanumeric(Input input) {
    return isAlphanumeric(input.peek().getByte())
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