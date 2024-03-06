package com.mikosik.stork.build.compile;

import static com.mikosik.stork.build.link.Bridge.stork;
import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.common.Peekerator.peekerator;
import static com.mikosik.stork.common.Throwables.fail;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.Lambda.lambda;
import static com.mikosik.stork.model.Module.module;
import static com.mikosik.stork.model.Parameter.parameter;
import static com.mikosik.stork.model.Quote.quote;
import static com.mikosik.stork.model.Variable.variable;

import java.util.Iterator;
import java.util.LinkedList;

import com.mikosik.stork.common.Peekerator;
import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Lambda;
import com.mikosik.stork.model.Module;

public class Compilation {
  private final Peekerator<Token> input;

  private Compilation(Peekerator<Token> input) {
    this.input = input;
  }

  public static Compilation compilation(Iterator<Token> input) {
    return new Compilation(peekerator(input));
  }

  public Module compile() {
    var definitions = new LinkedList<Definition>();
    while (hasNext()) {
      definitions.add(compileDefinition());
    }
    return module(definitions);
  }

  private Definition compileDefinition() {
    var name = nextLabel();
    var body = compileBody();
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
    var symbol = peekSymbol();
    if (symbol == '(') {
      return compileLambda();
    } else {
      return fail("unexpected character [%c]", symbol);
    }
  }

  private Expression compileInvocation() {
    var result = compileVariable();
    while (hasNext() && peekSymbol() == '(') {
      next();
      var argument = compileExpression();
      check(nextSymbol() == ')');
      result = application(result, argument);
    }
    return result;
  }

  private Expression compileVariable() {
    return variable(nextLabel());
  }

  private Lambda compileLambda() {
    check(nextSymbol() == '(');
    var parameter = parameter(nextLabel());
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
    var body = compileExpression();
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
