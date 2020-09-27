package com.mikosik.stork.testing;

import static com.mikosik.stork.common.Chains.chainOf;
import static com.mikosik.stork.common.Chains.map;
import static com.mikosik.stork.testing.Asserter.asserter;
import static com.mikosik.stork.tool.comp.WirableComputer.computer;
import static com.mikosik.stork.tool.link.DefaultLinker.defaultLinker;
import static com.mikosik.stork.tool.link.NoncollidingLinker.noncolliding;
import static java.lang.String.format;
import static org.quackery.Case.newCase;

import org.quackery.Test;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.lib.Modules;
import com.mikosik.stork.tool.comp.Computer;
import com.mikosik.stork.tool.link.Linker;

public class ModuleTest {
  public static Test moduleTest(String question, String answer) {
    String name = format("%s = %s", question, answer);
    return newCase(name, () -> run(question, answer));
  }

  private static void run(String question, String answer) {
    Chain<Module> modules = map(Modules::module, chainOf(
        "opcode.stork",
        "boolean.stork",
        "integer.stork",
        "stream.stork",
        "optional.stork",
        "function.stork"));
    Linker linker = noncolliding(defaultLinker());
    Module linkedModule = linker.link(modules);
    Computer computer = computer()
        .module(linkedModule)
        .opcoding()
        .substituting()
        .stacking()
        .interruptible()
        .wire(MockingComputer::mocking)
        .humane()
        .exhausted()
        .looping();

    asserter(computer)
        .assertEqual(question, answer);
  }
}
