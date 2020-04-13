package com.mikosik.stork;

import static com.mikosik.stork.TestRunner.testRunner;
import static com.mikosik.stork.TestStorkLibraries.testStorkLibraries;
import static com.mikosik.stork.testing.MoreReports.filter;
import static com.mikosik.stork.testing.MoreReports.formatExceptions;
import static org.quackery.Suite.suite;
import static org.quackery.report.Reports.count;
import static org.quackery.report.Reports.format;
import static org.quackery.run.Runners.run;

import org.quackery.Test;

public class RunAllTests {
  public static void main(String[] args) {
    Test test = suite("all tests")
        .add(testRunner())
        .add(testStorkLibraries());
    Test report = run(test);

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
