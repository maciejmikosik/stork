package com.mikosik.stork;

import static com.mikosik.stork.common.Chain.add;
import static com.mikosik.stork.common.Chains.chainOf;
import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.data.model.Definition.definition;
import static com.mikosik.stork.data.model.Module.module;
import static com.mikosik.stork.data.model.Variable.variable;
import static com.mikosik.stork.data.model.Verbs.verb;
import static com.mikosik.stork.tool.Default.compileDefinition;
import static com.mikosik.stork.tool.Default.compileExpression;
import static com.mikosik.stork.tool.common.Translate.asJavaBigInteger;
import static com.mikosik.stork.tool.link.DefaultLinker.defaultLinker;
import static com.mikosik.stork.tool.link.NoncollidingLinker.noncolliding;
import static com.mikosik.stork.tool.link.OverridingLinker.overriding;
import static com.mikosik.stork.tool.link.VerbModule.verbModule;
import static com.mikosik.stork.tool.run.ExhaustedRunner.exhausted;
import static com.mikosik.stork.tool.run.ModuleRunner.runner;
import static com.mikosik.stork.tool.run.Stepper.stepper;
import static java.lang.String.format;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.tool.link.Linker;
import com.mikosik.stork.tool.run.Runner;

public class Program {
  private final String main;
  private final Chain<Module> modules;

  private Program(String main, Chain<Module> modules) {
    this.main = main;
    this.modules = modules;
  }

  public static Program program(String main, Chain<Module> modules) {
    return new Program(main, modules);
  }

  public void run() {
    Linker linker = overriding(verbModule(), noncolliding(defaultLinker()));
    Runner moduleRunner = runner(linker.link(add(programModule(), modules)));
    Runner runner = exhausted(stepper(moduleRunner));
    runner.run(compileExpression(format("writeStream(%s)", main)));
  }

  private static Module programModule() {
    Definition writeByte = definition("writeByte", verb("writeByte", expression -> {
      int oneByte = asJavaBigInteger(expression).intValueExact();
      check(0 <= oneByte && oneByte <= 255);
      System.out.write(oneByte);
      System.out.flush();
      return variable("self");
    }));
    Definition writeStream = compileDefinition(""
        + "writeStream(stream) {"
        + "  stream"
        + "    ((head)(tail) {"
        + "      writeByte(head)(writeStream(tail))"
        + "    })"
        + "    (none)"
        + "}");
    return module(chainOf(writeByte, writeStream));
  }
}
