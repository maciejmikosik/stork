package com.mikosik.stork;

import static com.mikosik.stork.common.Chain.chainOf;
import static com.mikosik.stork.common.Input.input;
import static com.mikosik.stork.common.Input.resource;
import static com.mikosik.stork.common.Output.output;
import static com.mikosik.stork.front.program.Program.program;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.tool.compile.DefaultCompiler.defaultCompiler;

import com.mikosik.stork.common.Input;
import com.mikosik.stork.front.program.Program;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.tool.compile.Compiler;

public class RunDemo {
  public static void main(String[] args) {
    Compiler<Module> compiler = defaultCompiler();
    Module module = compiler.compile(file("demo.stork").buffered());
    Program program = program(variable("main"), chainOf(module));
    program.run(input(System.in))
        .pumpToAndFlush(output(System.out));
  }

  private static Input file(String name) {
    return resource(RunDemo.class, name);
  }
}
