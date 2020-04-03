package com.mikosik.lang;

import static com.mikosik.lang.common.Stream.stream;
import static com.mikosik.lang.model.runtime.Application.application;
import static com.mikosik.lang.model.runtime.Lambda.lambda;
import static com.mikosik.lang.model.runtime.Library.library;
import static com.mikosik.lang.model.runtime.Primitive.primitive;
import static com.mikosik.lang.model.runtime.Variable.variable;
import static com.mikosik.lang.tool.Compiler.compileApplication;
import static com.mikosik.lang.tool.Parser.parse;
import static com.mikosik.lang.tool.Printer.printer;
import static com.mikosik.lang.tool.Runner.runner;

import java.math.BigInteger;

import com.mikosik.lang.model.runtime.Expression;
import com.mikosik.lang.model.runtime.Library;
import com.mikosik.lang.model.syntax.Sentence;
import com.mikosik.lang.tool.Printer;
import com.mikosik.lang.tool.Runner;

public class Demo {
  private static Library library = library();
  private static Printer printer = printer();
  private static Runner runner = runner(library);

  public static void main(String[] args) {
    String f = "f";
    String x = "x";
    String y = "y";
    String head = "head";
    String tail = "tail";
    String vSome = "vSome";
    String vNone = "vNone";
    String some = "some";
    String none = "none";

    library.define("flip", l(f, l(x, l(y, compile("f(y)(x)")))));
    library.define("some", l(head, l(tail, l(vSome, l(vNone, compile("vSome(head)(tail)"))))));
    library.define("none", l(vSome, l(vNone, vNone)));

    show(ap("flip", n(1), n(2), n(3)));
    show(ap(some, n(1), ap(some, n(2), variable(none))));
    show(variable(some));
    show(variable(none));
  }

  private static Expression ap(Expression function, Expression... arguments) {
    Expression result = function;
    for (Expression argument : arguments) {
      result = application(result, argument);
    }
    return result;
  }

  private static Expression ap(String variable, Expression... arguments) {
    return ap(variable(variable), arguments);
  }

  private static Expression l(String parameter, Expression body) {
    return lambda(parameter, body);
  }

  private static Expression l(String parameter, String body) {
    return lambda(parameter, variable(body));
  }

  private static Expression n(int value) {
    return primitive(BigInteger.valueOf(value));
  }

  private static Expression compile(String source) {
    Sentence sentence = parse(stream(source));
    return compileApplication(sentence);
  }

  private static void show(Expression expression) {
    System.out.println(printer.print(expression));
    System.out.println(printer.print(runner.run(expression)));
  }
}
