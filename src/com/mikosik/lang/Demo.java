package com.mikosik.lang;

import static com.mikosik.lang.common.Stream.stream;
import static com.mikosik.lang.model.runtime.Library.library;
import static com.mikosik.lang.tool.Compiler.compileDefinition;
import static com.mikosik.lang.tool.Compiler.compileExpression;
import static com.mikosik.lang.tool.Parser.parse;
import static com.mikosik.lang.tool.Printer.printer;
import static com.mikosik.lang.tool.Runner.runner;

import com.mikosik.lang.model.def.Definition;
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
    library.add(definition("flip(f)(x)(y){f(y)(x)}"));
    library.add(definition("some(head)(tail)(vSome)(vNone){vSome(head)(tail)}"));
    library.add(definition("none(vSome)(vNone){vNone}"));

    show(compile("flip(a)(b)(c)"));
    show(compile("some(a)(some(b)(none))"));
    show(compile("some"));
    show(compile("none"));
  }

  private static Expression compile(String source) {
    Sentence sentence = parse(stream(source));
    return compileExpression(sentence);
  }

  private static Definition definition(String source) {
    Sentence sentence = parse(stream(source));
    return compileDefinition(sentence);
  }

  private static void show(Expression expression) {
    System.out.println(printer.print(expression));
    System.out.println(printer.print(runner.run(expression)));
  }
}
