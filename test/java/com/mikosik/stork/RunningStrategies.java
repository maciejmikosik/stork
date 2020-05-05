package com.mikosik.stork;

import static com.mikosik.stork.tool.link.DefaultLinker.defaultLinker;
import static com.mikosik.stork.tool.link.NoncollidingLinker.noncolliding;
import static com.mikosik.stork.tool.link.OverridingLinker.overriding;
import static com.mikosik.stork.tool.link.VerbModule.verbModule;
import static com.mikosik.stork.tool.run.ExhaustedRunner.exhausted;
import static com.mikosik.stork.tool.run.ModuleRunner.runner;
import static com.mikosik.stork.tool.run.RecursiveRunner.recursiveRunner;
import static com.mikosik.stork.tool.run.Stepper.stepper;

import java.util.function.Function;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.tool.link.Linker;
import com.mikosik.stork.tool.run.Runner;

public class RunningStrategies {
  public static Function<Chain<Module>, Runner> steppingStrategy() {
    return new Function<Chain<Module>, Runner>() {
      public Runner apply(Chain<Module> modules) {
        Linker linker = overriding(verbModule(), noncolliding(defaultLinker()));
        return exhausted(stepper(runner(linker.link(modules))));
      }

      public String toString() {
        return "stepping strategy";
      }
    };
  }

  public static Function<Chain<Module>, Runner> recursiveStrategy() {
    return new Function<Chain<Module>, Runner>() {
      public Runner apply(Chain<Module> modules) {
        Linker linker = overriding(verbModule(), noncolliding(defaultLinker()));
        return recursiveRunner(runner(linker.link(modules)));
      }

      public String toString() {
        return "recursive strategy";
      }
    };
  }
}
