package com.mikosik.stork.build.compile;

import static com.mikosik.stork.build.link.Bridge.stork;
import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.common.Throwables.fail;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.Lambda.lambda;
import static com.mikosik.stork.model.Module.module;
import static com.mikosik.stork.model.Parameter.parameter;
import static com.mikosik.stork.model.Quote.quote;
import static com.mikosik.stork.model.Variable.variable;

import java.util.LinkedList;
import java.util.List;

import com.mikosik.stork.common.PeekableIterator;
import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Lambda;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Parameter;
import com.mikosik.stork.model.Variable;

public class Compilation {
  private final PeekableIterator<Token> input;

  private Compilation(PeekableIterator<Token> input) {
    this.input = input;
  }

  public static Compilation compilation(PeekableIterator<Token> input) {
    return new Compilation(input);
  }

  public Module compile() {
    List<Definition> definitions = new LinkedList<>();
    while (hasNext()) {
      definitions.add(compileDefinition());
    }
    return module(definitions);
  }

  private Definition compileDefinition() {
    String name = nextLabel();
    Expression body = compileBody();
    return definition(name, body);
  }

  private Expression compileExpression() {
    if (!hasNext()) {
      return fail("unexpected end of stream");
    }
    var token = peek();
    if (token instanceof StringLiteral literal) {
      next();
      return quote(literal.string);
    } else if (token instanceof Label label) {
      return compileInvocation();
    } else if (token instanceof IntegerLiteral literal) {
      next();
      return stork(literal.value);
    }
    byte oneByte = peekByte();
    if (oneByte == '(') {
      return compileLambda();
    } else {
      return fail("unexpected character [%c]", oneByte);
    }
  }

  private Expression compileInvocation() {
    Expression result = compileVariable();
    while (hasNext() && peekByte() == '(') {
      next();
      Expression argument = compileExpression();
      check(nextByte() == ')');
      result = application(result, argument);
    }
    return result;
  }

  private Variable compileVariable() {
    return variable(nextLabel());
  }

  private Lambda compileLambda() {
    check(nextByte() == '(');
    Parameter parameter = parameter(nextLabel());
    check(nextByte() == ')');
    return lambda(parameter, compileBody());
  }

  private Expression compileBody() {
    var oneByte = (byte) peekByte();
    if (oneByte == '(') {
      return compileLambda();
    } else if (oneByte == '{') {
      return compileScope();
    } else {
      return fail("expected ( or { but was %c", oneByte);
    }
  }

  private Expression compileScope() {
    check(nextByte() == '{');
    Expression body = compileExpression();
    check(nextByte() == '}');
    return body;
  }

  private boolean hasNext() {
    return input.hasNext();
  }

  private Token next() {
    return input.next();
  }

  private String nextLabel() {
    return ((Label) next()).string;
  }

  private Byte nextByte() {
    return ((ByteToken) next()).value;
  }

  private Token peek() {
    return input.peek();
  }

  private Byte peekByte() {
    return ((ByteToken) peek()).value;
  }
}
