package com.mikosik.stork.build.compile;

import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.common.Logic.not;
import static com.mikosik.stork.common.PeekableIterator.peekable;
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

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.IntPredicate;

import com.mikosik.stork.common.PeekableIterator;
import com.mikosik.stork.common.io.Ascii;
import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Lambda;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Parameter;
import com.mikosik.stork.model.Variable;

public class Compiler {
  public Module compile(Iterator<Byte> input) {
    return compile(peekable(input));
  }

  private Module compile(PeekableIterator<Byte> input) {
    List<Definition> definitions = new LinkedList<>();
    while (input.hasNext()) {
      skipWhitespaces(input);
      definitions.add(compileDefinition(input));
      skipWhitespaces(input);
    }
    return module(definitions);
  }

  private Definition compileDefinition(PeekableIterator<Byte> input) {
    String name = compileAlphanumeric(input);
    skipWhitespaces(input);
    Expression body = compileBody(input);
    return definition(name, body);
  }

  private Expression compileExpression(PeekableIterator<Byte> input) {
    skipWhitespaces(input);
    if (!input.hasNext()) {
      return fail("unexpected end of stream");
    }
    byte oneByte = input.peek();
    if (isNumeric(oneByte)) {
      return compileInteger(input);
    } else if (isLetter(oneByte)) {
      return compileInvocation(input);
    } else if (isDoubleQuote(oneByte)) {
      return compileQuote(input);
    } else if (oneByte == '(') {
      return compileLambda(input);
    } else {
      return fail("unexpected character [%c]", oneByte);
    }
  }

  private Expression compileInteger(PeekableIterator<Byte> input) {
    return integer(new BigInteger(compileAlphanumeric(input)));
  }

  private Expression compileInvocation(PeekableIterator<Byte> input) {
    Expression result = compileVariable(input);
    skipWhitespaces(input);
    while (input.hasNext() && input.peek() == '(') {
      input.next();
      skipWhitespaces(input);
      Expression argument = compileExpression(input);
      skipWhitespaces(input);
      check(input.next() == ')');
      result = application(result, argument);
      skipWhitespaces(input);
    }
    return result;
  }

  private Variable compileVariable(PeekableIterator<Byte> input) {
    return variable(compileAlphanumeric(input));
  }

  private Expression compileQuote(PeekableIterator<Byte> input) {
    check(input.next() == DOUBLE_QUOTE);
    var bytes = readAll(not(Ascii::isDoubleQuote), input);
    check(input.next() == DOUBLE_QUOTE);
    return quote(ascii(bytes));
  }

  private Lambda compileLambda(PeekableIterator<Byte> input) {
    check(input.next() == '(');
    skipWhitespaces(input);
    Parameter parameter = parameter(compileAlphanumeric(input));
    skipWhitespaces(input);
    check(input.next() == ')');
    skipWhitespaces(input);
    return lambda(parameter, compileBody(input));
  }

  private Expression compileBody(PeekableIterator<Byte> input) {
    var oneByte = (byte) input.peek();
    if (oneByte == '(') {
      return compileLambda(input);
    } else if (oneByte == '{') {
      return compileScope(input);
    } else {
      return fail("expected ( or { but was %c", oneByte);
    }
  }

  private Expression compileScope(PeekableIterator<Byte> input) {
    check(input.next() == '{');
    skipWhitespaces(input);
    Expression body = compileExpression(input);
    skipWhitespaces(input);
    check(input.next() == '}');
    return body;
  }

  private String compileAlphanumeric(PeekableIterator<Byte> input) {
    return isAlphanumeric(input.peek())
        ? ascii(readAll(Ascii::isAlphanumeric, input))
        : fail("expected alphanumeric but was %c", input.peek());
  }

  private void skipWhitespaces(PeekableIterator<Byte> input) {
    readAll(Ascii::isWhitespace, input);
  }

  private static byte[] readAll(
      IntPredicate condition,
      PeekableIterator<Byte> iterator) {
    var output = new ByteArrayOutputStream();
    while (iterator.hasNext() && condition.test(iterator.peek())) {
      output.write(iterator.next());
    }
    return output.toByteArray();
  }
}
