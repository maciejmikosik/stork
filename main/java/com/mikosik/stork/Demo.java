package com.mikosik.stork;

import static com.mikosik.stork.common.Chain.chainOf;
import static com.mikosik.stork.lib.Libraries.library;
import static com.mikosik.stork.tool.Default.compileExpression;
import static com.mikosik.stork.tool.Default.compileLibrary;
import static com.mikosik.stork.tool.Linker.link;
import static com.mikosik.stork.tool.Printer.print;
import static com.mikosik.stork.tool.run.Runner.runner;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Library;
import com.mikosik.stork.tool.run.Runner;

public class Demo {
  public static void main(String[] args) {
    Chain<Library> libraries = chainOf(
        library("integer.stork"),
        compileLibrary("main { add(2)(3) }"));
    Runner runner = runner(link(libraries));

    Expression main = compileExpression("main");
    System.out.println(print(runner.run(main)));
  }
}
