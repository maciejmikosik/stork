package com.mikosik.stork;

import static com.mikosik.stork.MoreReports.formatExceptions;
import static com.mikosik.stork.TestSimpleFunctions.testSimpleFunctions;
import static org.quackery.report.Reports.format;
import static org.quackery.run.Runners.run;

import org.quackery.Test;

public class RunAllTests {
  public static void main(String[] args) {
    Test report = run(testSimpleFunctions());

    System.out.println(formatExceptions(report));
    System.out.println(format(report));
  }
}
