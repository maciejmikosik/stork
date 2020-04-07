package com.mikosik.lang;

import static com.mikosik.lang.MoreReports.formatExceptions;
import static com.mikosik.lang.TestSimpleFunctions.testSimpleFunctions;
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
