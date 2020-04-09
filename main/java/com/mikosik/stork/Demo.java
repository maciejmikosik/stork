package com.mikosik.stork;

import static com.mikosik.stork.common.Stream.stream;
import static com.mikosik.stork.lib.Libraries.library;
import static com.mikosik.stork.tool.Compiler.compileExpression;
import static com.mikosik.stork.tool.Parser.parse;
import static com.mikosik.stork.tool.Printer.printer;
import static com.mikosik.stork.tool.Runner.runner;
import static com.mikosik.stork.tool.Runtime.runtime;

import com.mikosik.stork.model.runtime.Expression;
import com.mikosik.stork.model.syntax.Sentence;
import com.mikosik.stork.tool.Printer;
import com.mikosik.stork.tool.Runner;

public class Demo {

  public static void main(String[] args) {
    Runner runner = runner(runtime(
        library("lang.stork"),
        library("core.stork")));

    show("main", runner);
  }

  private static void show(String source, Runner runner) {
    Sentence sentence = parse(stream(source));
    Expression expression = compileExpression(sentence);

    Printer printer = printer();
    System.out.println(printer.print(expression));
    System.out.println(printer.print(runner.run(expression)));
    System.out.println("----------------------------------");
  }
}
