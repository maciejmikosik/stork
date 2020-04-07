package com.mikosik.stork;

import static com.mikosik.stork.CoreLibrary.installCore;
import static com.mikosik.stork.common.Stream.stream;
import static com.mikosik.stork.model.def.Library.library;
import static com.mikosik.stork.tool.Compiler.compileDefinition;
import static com.mikosik.stork.tool.Compiler.compileExpression;
import static com.mikosik.stork.tool.Parser.parse;
import static com.mikosik.stork.tool.Printer.printer;
import static com.mikosik.stork.tool.Runner.runner;
import static java.util.Arrays.stream;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import com.mikosik.stork.model.def.Definition;
import com.mikosik.stork.model.def.Library;
import com.mikosik.stork.model.runtime.Expression;
import com.mikosik.stork.model.syntax.Sentence;
import com.mikosik.stork.tool.Printer;
import com.mikosik.stork.tool.Runner;

public class Demo {
  private static Library library = library();

  public static void main(String[] args) {
    stream(readFile("core.stork").split("\n\n"))
        .map(String::trim)
        .filter(source -> source.length() > 0)
        .map(Demo::definition)
        .forEach(library::add);

    installCore(library);

    show("main");
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

  private static String readFile(String name) {
    try (InputStream input = new BufferedInputStream(Demo.class.getResourceAsStream(name))) {
      StringBuilder builder = new StringBuilder();
      while (input.available() > 0) {
        builder.append((char) input.read());
      }
      return builder.toString();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
