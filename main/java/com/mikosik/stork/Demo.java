package com.mikosik.stork;

import static com.mikosik.stork.common.Chain.chainOf;
import static com.mikosik.stork.data.model.Library.library;
import static com.mikosik.stork.lib.Libraries.library;
import static com.mikosik.stork.tool.Compiler.compileDefinition;
import static com.mikosik.stork.tool.Compiler.compileExpression;
import static com.mikosik.stork.tool.Parser.parse;
import static com.mikosik.stork.tool.Printer.printer;
import static com.mikosik.stork.tool.Runner.runner;
import static com.mikosik.stork.tool.Runtime.runtime;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Library;
import com.mikosik.stork.tool.Printer;
import com.mikosik.stork.tool.Runner;

public class Demo {
  public static void main(String[] args) {
    Library mainLibrary = library(chainOf(
        compileDefinition(parse("main { add(2)(3) }"))));
    Chain<Library> libraries = chainOf(
        library("integer.stork"),
        library("core.stork"),
        mainLibrary);
    Runner runner = runner(runtime(libraries));

    Printer printer = printer();
    Expression main = compileExpression(parse("main"));
    System.out.println(printer.print(runner.run(main)));
  }
}
