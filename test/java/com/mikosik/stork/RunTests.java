package com.mikosik.stork;

import static com.mikosik.stork.TestEverything.testEverything;
import static com.mikosik.stork.testing.MoreReports.filter;
import static com.mikosik.stork.testing.MoreReports.formatExceptions;
import static java.lang.String.format;
import static org.quackery.report.Reports.count;
import static org.quackery.report.Reports.format;
import static org.quackery.run.Runners.run;

import org.quackery.Test;

public class RunTests {
  public static void main(String[] args) {
    Test test = testEverything();
    Test report = run(test);
    print(report);
  }

  private static void print(Test report) {
    int total = count(Throwable.class, report);
    if (total == 0) {
      System.out.print(format(""
          + "%s\n"
          + "no failures\n",
          format(report)));
    } else {
      Test failed = filter(Throwable.class, report).get();
      System.err.print(format(""
          + "%s\n"
          + "%s\n",
          format(failed),
          formatExceptions(failed)));
    }
  }
}