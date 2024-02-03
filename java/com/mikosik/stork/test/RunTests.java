package com.mikosik.stork.test;

import static com.mikosik.stork.test.MoreReports.formatExceptions;
import static com.mikosik.stork.test.QuackeryHelper.count;
import static com.mikosik.stork.test.QuackeryHelper.filterFailed;
import static com.mikosik.stork.test.cases.TestEverything.testEverything;
import static java.lang.String.format;
import static java.time.Duration.between;
import static java.time.Instant.now;
import static org.quackery.report.Reports.format;
import static org.quackery.run.Runners.run;

import org.quackery.Test;

/**
 * Logging tests that use logbuddy require those options. They allow logbuddy to
 * read private fields of classes in java.* package.
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
    var start = now();
    var test = testEverything();
    var building = between(start, now());

    start = now();
    var report = run(test);
    var running = between(start, now());

    print(report);
    System.out.println("""

        building: %sms
         running: %sms
        """.formatted(
        building.toMillis(),
        running.toMillis()));
  }

  private static void print(Test report) {
    var failed = filterFailed(report);
    if (count(failed) == 0) {
      System.out.print(format(""
          + "%s\n"
          + "no failures\n",
          format(report)));
    } else {
      System.err.print(format(""
          + "%s\n"
          + "%s\n",
          format(failed),
          formatExceptions(failed)));
    }
  }
}
