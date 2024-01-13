package com.mikosik.stork.test;

import static com.mikosik.stork.test.MoreReports.filter;
import static com.mikosik.stork.test.MoreReports.formatExceptions;
import static com.mikosik.stork.test.cases.TestEverything.testEverything;
import static java.lang.String.format;
import static org.quackery.report.Reports.count;
import static org.quackery.report.Reports.format;
import static org.quackery.run.Runners.run;

import org.quackery.Test;

/**
 * Logging tests that use logbuddy require those options. They allow logbuddy to read private fields
 * of classes in java.* package.
 *
 * <pre>
--add-opens=java.base/java.lang=ALL-UNNAMED
--add-opens=java.base/java.util=ALL-UNNAMED
--add-opens=java.base/java.math=ALL-UNNAMED
--add-opens=java.base/java.lang.ref=ALL-UNNAMED
--add-opens=java.base/java.util.concurrent.locks=ALL-UNNAMED
 * </pre>
 */
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
