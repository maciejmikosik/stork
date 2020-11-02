package com.mikosik.stork;

import static com.mikosik.stork.common.Chain.chainOf;
import static com.mikosik.stork.common.InputOutput.pump;
import static com.mikosik.stork.common.InputOutput.readResource;
import static com.mikosik.stork.core.CoreModule.coreModule;
import static com.mikosik.stork.data.model.Variable.variable;
import static com.mikosik.stork.main.Program.program;
import static com.mikosik.stork.tool.compile.Modeler.modelModule;
import static com.mikosik.stork.tool.compile.Parser.parse;
import static com.mikosik.stork.tool.link.WirableLinker.linker;

import java.io.InputStream;

import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.main.Program;
import com.mikosik.stork.tool.link.Linker;

public class RunDemo {
  public static void main(String[] args) {
    Linker linker = linker()
        .unique()
        .coherent();
    Module module = linker.link(chainOf(
        modelModule(parse(readResource(RunDemo.class, "demo.stork"))),
        coreModule()));
    Program program = program(variable("main"), module);
    InputStream input = program.run();
    pump(input, System.out);
    System.out.flush();
  }
}
