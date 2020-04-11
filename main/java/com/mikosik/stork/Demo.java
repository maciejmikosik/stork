package com.mikosik.stork;

import static com.mikosik.stork.common.Chain.chainOf;
import static com.mikosik.stork.lib.Libraries.library;
import static com.mikosik.stork.tool.Default.compileExpression;
import static com.mikosik.stork.tool.Default.compileLibrary;
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
    Chain<Library> libraries = chainOf(
        library("integer.stork"),
        library("core.stork"),
        compileLibrary("main { add(2)(3) }"));
    Runner runner = runner(runtime(libraries));

    Printer printer = printer();
    Expression main = compileExpression("main");
    System.out.println(printer.print(runner.run(main)));
  }
}
