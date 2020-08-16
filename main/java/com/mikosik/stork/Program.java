package com.mikosik.stork;

import static com.mikosik.stork.common.Chain.add;
import static com.mikosik.stork.common.Chains.chainOf;
import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.data.model.Definition.definition;
import static com.mikosik.stork.data.model.Module.module;
import static com.mikosik.stork.data.model.Variable.variable;
import static com.mikosik.stork.data.model.comp.Computation.computation;
import static com.mikosik.stork.tool.Default.compileDefinition;
import static com.mikosik.stork.tool.Default.compileExpression;
import static com.mikosik.stork.tool.common.Translate.asJavaBigInteger;
import static com.mikosik.stork.tool.comp.DefaultComputer.computer;
import static com.mikosik.stork.tool.comp.ExhaustedComputer.exhausted;
import static com.mikosik.stork.tool.comp.StackingComputer.stacking;
import static com.mikosik.stork.tool.comp.SteppingComputer.stepping;
import static com.mikosik.stork.tool.comp.VariableComputer.variable;
import static com.mikosik.stork.tool.comp.VerbComputer.verb;
import static com.mikosik.stork.tool.link.DefaultLinker.defaultLinker;
import static com.mikosik.stork.tool.link.NoncollidingLinker.noncolliding;
import static com.mikosik.stork.tool.link.OverridingLinker.overriding;
import static com.mikosik.stork.tool.link.VerbModule.verbModule;
import static java.lang.String.format;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Definition;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.data.model.Verb;
import com.mikosik.stork.tool.comp.Computer;
import com.mikosik.stork.tool.link.Linker;

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
    Module linkedModule = linker.link(add(programModule(), modules));
    Computer computer = exhausted(stepping(stacking(variable(linkedModule, verb(computer())))));
    computer.compute(computation(compileExpression(format("writeStream(%s)", main))));
  }

  private static Module programModule() {
    Definition writeByte = definition("writeByte", new Verb() {
      public String toString() {
        return "$writeByte";
      }

      public Expression apply(Expression argument) {
        int oneByte = asJavaBigInteger(argument).intValueExact();
        check(0 <= oneByte && oneByte <= 255);
        System.out.write(oneByte);
        System.out.flush();
        return variable("self");
      }
    });
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
