package com.mikosik.stork;

import static com.mikosik.stork.TestRunner.testRunner;
import static com.mikosik.stork.TestStorkModules.testStorkModules;
import static com.mikosik.stork.testing.MoreReports.filter;
import static com.mikosik.stork.testing.MoreReports.formatExceptions;
import static com.mikosik.stork.tool.link.DefaultLinker.defaultLinker;
import static com.mikosik.stork.tool.link.NoncollidingLinker.noncolliding;
import static com.mikosik.stork.tool.link.OverridingLinker.overriding;
import static com.mikosik.stork.tool.link.VerbModule.verbModule;
import static com.mikosik.stork.tool.run.ExhaustedRunner.exhausted;
import static com.mikosik.stork.tool.run.ModuleRunner.runner;
import static com.mikosik.stork.tool.run.Stepper.stepper;
import static org.quackery.Suite.suite;
import static org.quackery.help.Helpers.traverse;
import static org.quackery.report.Reports.count;
import static org.quackery.report.Reports.format;
import static org.quackery.run.Runners.run;

import java.util.function.Function;

import org.quackery.Test;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.testing.StorkTest;
import com.mikosik.stork.tool.link.Linker;
import com.mikosik.stork.tool.run.Runner;

public class RunAllTests {
  public static void main(String[] args) {
    Function<Chain<Module>, Runner> steppingStrategy = modules -> {
      Linker linker = overriding(verbModule(), noncolliding(defaultLinker()));
      return exhausted(stepper(runner(linker.link(modules))));
    };

    Test test = suite("all tests")
        .add(use(steppingStrategy, standardTests("using stepping strategy")));
    Test report = run(test);

    printAndExit(report);
  }

  private static Test standardTests(String suiteName) {
    return suite(suiteName)
        .add(testRunner())
        .add(testStorkModules());
  }

  private static Test use(Function<Chain<Module>, Runner> runningStrategy, Test root) {
    return traverse(root, test -> {
      return test instanceof StorkTest
          ? ((StorkTest) test).runningStrategy(runningStrategy)
          : test;
    });
  }

  private static void printAndExit(Test report) {
    int total = count(Throwable.class, report);
    if (total == 0) {
      System.out.println(format(report));
      System.out.println("");
      System.out.println("no failures");
      System.exit(0);
    } else {
      Test failed = filter(Throwable.class, report).get();
      System.err.println(format(failed));
      System.err.println("");
      System.err.println(formatExceptions(failed));
      System.exit(1);
    }
  }
}
