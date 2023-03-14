package com.mikosik.stork.compile;

import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.common.Logic.not;
import static com.mikosik.stork.common.Throwables.fail;
import static com.mikosik.stork.common.io.Ascii.DOUBLE_QUOTE;
import static com.mikosik.stork.common.io.Ascii.ascii;
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

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import com.mikosik.stork.common.io.Ascii;
import com.mikosik.stork.common.io.Input;
import com.mikosik.stork.common.io.MaybeByte;
import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Lambda;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Parameter;
import com.mikosik.stork.model.Variable;

public class Compiler {
  public Module compileModule(Input input) {
    List<Definition> definitions = new LinkedList<>();
    while (input.peek().hasByte()) {
      skipWhitespaces(input);
      definitions.add(compileDefinition(input));
      skipWhitespaces(input);
    }
    return module(definitions);
  }

  public Definition compileDefinition(Input input) {
    String name = compileAlphanumeric(input);
    skipWhitespaces(input);
    Expression body = compileBody(input);
    return definition(name, body);
  }

  public Expression compileExpression(Input input) {
    skipWhitespaces(input);
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
      return fail("unexpected character [%c]", maybeByte.getByte());
    }
  }

  protected Expression compileInteger(Input input) {
    return integer(new BigInteger(compileAlphanumeric(input)));
  }

  protected Expression compileInvocation(Input input) {
    Expression result = compileVariable(input);
    skipWhitespaces(input);
    while (input.peek().hasByte()
        && input.peek().getByte() == '(') {
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
    byte[] bytes = input.readAllBytes(not(Ascii::isDoubleQuote));
    check(input.read().getByte() == DOUBLE_QUOTE);
    return quote(ascii(bytes));
  }

  protected Lambda compileLambda(Input input) {
    check(input.read().getByte() == '(');
    skipWhitespaces(input);
    Parameter parameter = parameter(compileAlphanumeric(input));
    skipWhitespaces(input);
    check(input.read().getByte() == ')');
    skipWhitespaces(input);
    return lambda(parameter, compileBody(input));
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
        ? ascii(input.readAllBytes(Ascii::isAlphanumeric))
        : fail("expected alphanumeric but was %c", input.peek());
  }

  protected void skipWhitespaces(Input input) {
    input.readAllBytes(Ascii::isWhitespace);
  }
}
