package com.mikosik.stork;

import static com.mikosik.stork.TestRunner.testRunner;
import static com.mikosik.stork.TestStorkLibraries.testStorkLibraries;
import static com.mikosik.stork.testing.MoreReports.formatExceptions;
import static org.quackery.Suite.suite;
import static org.quackery.report.Reports.format;
import static org.quackery.run.Runners.run;

import org.quackery.Test;

public class RunAllTests {
  public static void main(String[] args) {
    Test test = suite("all tests")
        .add(testRunner())
        .add(testStorkLibraries());
    Test report = run(test);

    System.out.println(formatExceptions(report));
    System.out.println(format(report));
  }
}
