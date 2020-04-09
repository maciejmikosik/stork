package com.mikosik.stork;

import static com.mikosik.stork.CoreLibrary.coreLibrary;
import static com.mikosik.stork.common.Stream.stream;
import static com.mikosik.stork.model.def.Library.library;
import static com.mikosik.stork.tool.Compiler.compileExpression;
import static com.mikosik.stork.tool.Parser.parse;
import static com.mikosik.stork.tool.Printer.printer;
import static com.mikosik.stork.tool.Runner.runner;
import static com.mikosik.stork.tool.Runtime.runtime;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;

import com.mikosik.stork.common.Stream;
import com.mikosik.stork.model.def.Definition;
import com.mikosik.stork.model.runtime.Expression;
import com.mikosik.stork.model.syntax.Sentence;
import com.mikosik.stork.tool.Compiler;
import com.mikosik.stork.tool.Parser;
import com.mikosik.stork.tool.Printer;
import com.mikosik.stork.tool.Runner;

public class Demo {

  public static void main(String[] args) {
    List<Definition> definitions = stream(readFile("core.stork").split("\n\n"))
        .map(String::trim)
        .filter(source -> source.length() > 0)
        .map(Stream::stream)
        .map(Parser::parse)
        .map(Compiler::compileDefinition)
        .collect(toList());

    Runner runner = runner(runtime(
        library(definitions),
        coreLibrary()));

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
