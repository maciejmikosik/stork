package com.mikosik.stork;

import static com.mikosik.stork.RunningStrategies.recursiveStrategy;
import static com.mikosik.stork.RunningStrategies.steppingStrategy;
import static com.mikosik.stork.TestBooleanModule.testBooleanModule;
import static com.mikosik.stork.TestFunctionModule.testFunctionModule;
import static com.mikosik.stork.TestIntegerModule.testIntegerModule;
import static com.mikosik.stork.TestOptionalModule.testOptionalModule;
import static com.mikosik.stork.TestRunner.testRunner;
import static org.quackery.Suite.suite;
import static org.quackery.help.Helpers.traverse;

import java.util.function.Function;

import org.quackery.Test;

import com.mikosik.stork.common.Chain;
import com.mikosik.stork.data.model.Module;
import com.mikosik.stork.testing.StorkTest;
import com.mikosik.stork.tool.run.Runner;

public class TestEverything {
  public static Test testEverythingUsingAllStrategies() {
    return suite("test everything")
        .add(testEverythingUsing(steppingStrategy()))
        .add(testEverythingUsing(recursiveStrategy()));
  }

  private static Test testEverythingUsing(Function<Chain<Module>, Runner> runningStrategy) {
    return use(runningStrategy, suite("using " + runningStrategy)
        .add(testRunner())
        .add(suite("stork modules")
            .add(testFunctionModule())
            .add(testBooleanModule())
            .add(testIntegerModule())
            .add(testOptionalModule())));
  }

  private static Test use(Function<Chain<Module>, Runner> runningStrategy, Test root) {
    return traverse(root, test -> {
      return test instanceof StorkTest
          ? ((StorkTest) test).runningStrategy(runningStrategy)
          : test;
    });
  }
}
