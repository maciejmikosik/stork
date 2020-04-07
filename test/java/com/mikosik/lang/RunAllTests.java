package com.mikosik.lang;

import static com.mikosik.lang.TestSimpleFunctions.testSimpleFunctions;
import static org.quackery.report.Reports.count;
import static org.quackery.report.Reports.format;
import static org.quackery.run.Runners.run;

import org.quackery.Test;

public class RunAllTests {
  public static void main(String[] args) {
    Test report = run(testSimpleFunctions());
    System.out.println(format(report));

    int failures = count(Throwable.class, report);
    if (failures > 0) {
      throw new AssertionError();
    }
  }
}
