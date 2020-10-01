package com.mikosik.stork.testing;

import static com.mikosik.stork.testing.Asserter.asserter;
import static com.mikosik.stork.tool.comp.WirableComputer.computer;
import static com.mikosik.stork.tool.link.Linker.coreModule;
import static java.lang.String.format;
import static org.quackery.Case.newCase;

import org.quackery.Test;

import com.mikosik.stork.tool.comp.Computer;

public class ModuleTest {
  private static final Computer computer = computer()
      .module(coreModule())
      .opcoding()
      .substituting()
      .stacking()
      .interruptible()
      .wire(MockingComputer::mocking)
      .humane()
      .exhausted()
      .looping();

  public static Test moduleTest(String question, String answer) {
    String name = format("%s = %s", question, answer);
    return newCase(name, () -> asserter(computer).assertEqual(question, answer));
  }
}
