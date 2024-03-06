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
    byte symbol = peekSymbol();
    if (symbol == '(') {
      return compileLambda();
    } else {
      return fail("unexpected character [%c]", symbol);
    }
  }

  private Expression compileInvocation() {
    Expression result = compileVariable();
    while (hasNext() && peekSymbol() == '(') {
      next();
      Expression argument = compileExpression();
      check(nextSymbol() == ')');
      result = application(result, argument);
    }
    return result;
  }

  private Variable compileVariable() {
    return variable(nextLabel());
  }

  private Lambda compileLambda() {
    check(nextSymbol() == '(');
    Parameter parameter = parameter(nextLabel());
    check(nextSymbol() == ')');
    return lambda(parameter, compileBody());
  }

  private Expression compileBody() {
    var symbol = peekSymbol();
    if (symbol == '(') {
      return compileLambda();
    } else if (symbol == '{') {
      return compileScope();
    } else {
      return fail("expected ( or { but was %c", symbol);
    }
  }

  private Expression compileScope() {
    check(nextSymbol() == '{');
    Expression body = compileExpression();
    check(nextSymbol() == '}');
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

  private byte nextSymbol() {
    return ((Symbol) next()).character;
  }

  private Token peek() {
    return input.peek();
  }

  private byte peekSymbol() {
    return ((Symbol) peek()).character;
  }
}
