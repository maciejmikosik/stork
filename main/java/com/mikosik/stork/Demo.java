package com.mikosik.stork;

import static com.mikosik.stork.common.Chains.chainOf;
import static com.mikosik.stork.lib.Modules.module;
import static com.mikosik.stork.tool.Default.compileExpression;
import static com.mikosik.stork.tool.Default.compileModule;
import static com.mikosik.stork.tool.DefaultLinker.defaultLinker;
import static com.mikosik.stork.tool.ImplementingVerbsLinker.implementingVerbs;
import static com.mikosik.stork.tool.run.ExhaustedRunner.exhausted;
import static com.mikosik.stork.tool.run.LoggingRunner.logging;
import static com.mikosik.stork.tool.run.ModuleRunner.runner;
import static com.mikosik.stork.tool.run.Stepper.stepper;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.tool.Linker;
import com.mikosik.stork.tool.run.Runner;

public class Demo {
  public static void main(String[] args) {
    Chain<Module> modules = chainOf(
        module("integer.stork"),
        compileModule("main { add(add(1)(2))(add(5)(10)) }"));
    Linker linker = implementingVerbs(defaultLinker());
    Runner moduleRunner = runner(linker.link(modules));
    Runner runner = exhausted(logging(stepper(moduleRunner)));
    Expression main = compileExpression("main");
    runner.run(main);
  }
}
