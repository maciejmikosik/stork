package com.mikosik.stork;

import static com.mikosik.stork.common.Stream.stream;
import static com.mikosik.stork.lib.Libraries.library;
import static com.mikosik.stork.model.def.Library.library;
import static com.mikosik.stork.tool.Compiler.compileDefinition;
import static com.mikosik.stork.tool.Compiler.compileExpression;
import static com.mikosik.stork.tool.Parser.parse;
import static com.mikosik.stork.tool.Printer.printer;
import static com.mikosik.stork.tool.Runner.runner;
import static com.mikosik.stork.tool.Runtime.runtime;
import static java.util.Arrays.asList;

import com.mikosik.stork.model.def.Library;
import com.mikosik.stork.model.runtime.Expression;
import com.mikosik.stork.tool.Printer;
import com.mikosik.stork.tool.Runner;

public class Demo {
  public static void main(String[] args) {
    Library mainLibrary = library(asList(
        compileDefinition(parse(stream("main { add(2)(3) }")))));
    Runner runner = runner(runtime(
        library("lang.stork"),
        library("core.stork"),
        mainLibrary));

    Printer printer = printer();
    Expression main = compileExpression(parse(stream("main")));
    System.out.println(printer.print(runner.run(main)));
  }
}
