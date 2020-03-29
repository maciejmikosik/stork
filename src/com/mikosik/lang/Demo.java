package com.mikosik.lang;

import static com.mikosik.lang.debug.Printer.printer;
import static com.mikosik.lang.model.Alias.alias;
import static com.mikosik.lang.model.Application.application;
import static com.mikosik.lang.model.Lambda.lambda;
import static com.mikosik.lang.model.Library.library;
import static com.mikosik.lang.model.Primitive.primitive;
import static com.mikosik.lang.run.Runner.runner;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

import java.math.BigInteger;

import com.mikosik.lang.debug.Printer;
import com.mikosik.lang.model.Alias;
import com.mikosik.lang.model.Expression;
import com.mikosik.lang.model.Library;
import com.mikosik.lang.run.Runner;

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

    library.define("flip", l(f, l(x, l(y, ap(f, y, x)))));
    library.define("some", l(head, l(tail, l(vSome, l(vNone, ap(vSome, head, tail))))));
    library.define("none", l(vSome, l(vNone, vNone)));

    show(ap("flip", n(1), n(2), n(3)));
    show(ap(some, n(1), ap(some, n(2), alias(none))));
    show(alias(some));
    show(alias(none));
  }

  private static Expression ap(Expression function, Expression... arguments) {
    Expression result = function;
    for (Expression argument : arguments) {
      result = application(result, argument);
    }
    return result;
  }

  private static Expression ap(String alias, Expression... arguments) {
    return ap(alias(alias), arguments);
  }

  private static Expression ap(String alias, String... arguments) {
    Expression[] args = stream(arguments)
        .map(Alias::alias)
        .collect(toList())
        .toArray(new Expression[0]);
    return ap(alias(alias), args);
  }

  private static Expression l(String parameter, Expression body) {
    return lambda(parameter, body);
  }

  private static Expression l(String parameter, String body) {
    return lambda(parameter, alias(body));
  }

  private static Expression n(int value) {
    return primitive(BigInteger.valueOf(value));
  }

  private static void show(Expression expression) {
    System.out.println(printer.print(expression));
    System.out.println(printer.print(runner.run(expression)));
  }
}
