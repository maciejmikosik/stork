package com.mikosik.lang;

import static com.mikosik.lang.common.Stream.stream;
import static com.mikosik.lang.model.def.Library.library;
import static com.mikosik.lang.model.runtime.Primitive.primitive;
import static com.mikosik.lang.tool.Compiler.compileDefinition;
import static com.mikosik.lang.tool.Compiler.compileExpression;
import static com.mikosik.lang.tool.Parser.parse;
import static com.mikosik.lang.tool.Printer.printer;
import static com.mikosik.lang.tool.Runner.runner;

import java.math.BigInteger;

import com.mikosik.lang.model.def.Definition;
import com.mikosik.lang.model.def.Library;
import com.mikosik.lang.model.runtime.Core;
import com.mikosik.lang.model.runtime.Expression;
import com.mikosik.lang.model.runtime.Primitive;
import com.mikosik.lang.model.syntax.Sentence;
import com.mikosik.lang.tool.Printer;
import com.mikosik.lang.tool.Runner;

public class Demo {
  private static Library library = library();

  public static void main(String[] args) {
    library.add(definition("flip(f)(x)(y){f(y)(x)}"));
    library.add(definition("some(head)(tail)(vSome)(vNone){vSome(head)(tail)}"));
    library.add(definition("none(vSome)(vNone){vNone}"));

    library.define("add", (Core) argumentA -> {
      BigInteger numberA = (BigInteger) ((Primitive) argumentA).object;
      return (Core) argumentB -> {
        BigInteger numberB = (BigInteger) ((Primitive) argumentB).object;
        return primitive(numberA.add(numberB));
      };
    });
    library.define("negate", (Core) argument -> {
      BigInteger bigInteger = (BigInteger) ((Primitive) argument).object;
      return primitive(bigInteger.negate());
    });

    library.add(definition("subtract(x){add(negate(x))}"));

    show("add(add(negate(1))(2))(add(3)(4))");
    show("flip(add)(5)(1)");
    show("subtract(5)(1)");
  }

  private static Definition definition(String source) {
    Sentence sentence = parse(stream(source));
    return compileDefinition(sentence);
  }

  private static void show(String source) {
    Sentence sentence = parse(stream(source));
    Expression expression = compileExpression(sentence);

    Printer printer = printer();
    Runner runner = runner(library);
    System.out.println(printer.print(expression));
    System.out.println(printer.print(runner.run(expression)));
    System.out.println("----------------------------------");
  }
}
