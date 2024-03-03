package com.mikosik.stork.build.compile;

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

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import com.mikosik.stork.common.PeekableIterator;
import com.mikosik.stork.common.io.Ascii;
import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Lambda;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Parameter;
import com.mikosik.stork.model.Variable;

public class Compilation {
  private final PeekableIterator<Byte> input;

  private Compilation(PeekableIterator<Byte> input) {
    this.input = input;
  }

  public static Compilation compilation(PeekableIterator<Byte> input) {
    return new Compilation(input);
  }

  public Module compile() {
    List<Definition> definitions = new LinkedList<>();
    while (hasNext()) {
      skipWhitespaces();
      definitions.add(compileDefinition());
      skipWhitespaces();
    }
    return module(definitions);
  }

  private Definition compileDefinition() {
    String name = compileAlphanumeric();
    skipWhitespaces();
    Expression body = compileBody();
    return definition(name, body);
  }

  private Expression compileExpression() {
    skipWhitespaces();
    if (!hasNext()) {
      return fail("unexpected end of stream");
    }
    byte oneByte = peek();
    if (isNumeric(oneByte)) {
      return compileInteger();
    } else if (isLetter(oneByte)) {
      return compileInvocation();
    } else if (isDoubleQuote(oneByte)) {
      return compileQuote();
    } else if (oneByte == '(') {
      return compileLambda();
    } else {
      return fail("unexpected character [%c]", oneByte);
    }
  }

  private Expression compileInteger() {
    return integer(new BigInteger(compileAlphanumeric()));
  }

  private Expression compileInvocation() {
    Expression result = compileVariable();
    skipWhitespaces();
    while (hasNext() && peek() == '(') {
      next();
      skipWhitespaces();
      Expression argument = compileExpression();
      skipWhitespaces();
      check(next() == ')');
      result = application(result, argument);
      skipWhitespaces();
    }
    return result;
  }

  private Variable compileVariable() {
    return variable(compileAlphanumeric());
  }

  private Expression compileQuote() {
    check(next() == DOUBLE_QUOTE);
    var bytes = readAll(not(Ascii::isDoubleQuote));
    check(next() == DOUBLE_QUOTE);
    return quote(ascii(bytes));
  }

  private Lambda compileLambda() {
    check(next() == '(');
    skipWhitespaces();
    Parameter parameter = parameter(compileAlphanumeric());
    skipWhitespaces();
    check(next() == ')');
    skipWhitespaces();
    return lambda(parameter, compileBody());
  }

  private Expression compileBody() {
    var oneByte = (byte) peek();
    if (oneByte == '(') {
      return compileLambda();
    } else if (oneByte == '{') {
      return compileScope();
    } else {
      return fail("expected ( or { but was %c", oneByte);
    }
  }

  private Expression compileScope() {
    check(next() == '{');
    skipWhitespaces();
    Expression body = compileExpression();
    skipWhitespaces();
    check(next() == '}');
    return body;
  }

  private String compileAlphanumeric() {
    return isAlphanumeric(peek())
        ? ascii(readAll(Ascii::isAlphanumeric))
        : fail("expected alphanumeric but was %c", peek());
  }

  private boolean hasNext() {
    return input.hasNext();
  }

  private Byte next() {
    return input.next();
  }

  private Byte peek() {
    return input.peek();
  }

  private void skipWhitespaces() {
    readAll(Ascii::isWhitespace);
  }

  private byte[] readAll(Predicate<Byte> condition) {
    var output = new ByteArrayOutputStream();
    while (hasNext() && condition.test(peek())) {
      output.write(next());
    }
    return output.toByteArray();
  }
}
