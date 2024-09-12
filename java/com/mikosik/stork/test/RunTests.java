package com.mikosik.stork.test;

import static com.mikosik.stork.build.link.VerifyModule.verify;
import static com.mikosik.stork.common.StandardOutput.err;
import static com.mikosik.stork.common.StandardOutput.out;
import static com.mikosik.stork.test.CoreLibrary.CORE_LIBRARY;
import static com.mikosik.stork.test.MoreReports.formatExceptions;
import static com.mikosik.stork.test.QuackeryHelper.count;
import static com.mikosik.stork.test.QuackeryHelper.filterFailed;
import static com.mikosik.stork.test.QuackeryHelper.nameOf;
import static com.mikosik.stork.test.cases.TestEverything.testEverything;
import static java.lang.System.exit;
import static org.quackery.Case.newCase;
import static org.quackery.report.Reports.format;
import static org.quackery.run.Runners.run;

import org.quackery.Test;

/**
 * Logging tests that use logbuddy require those options. They allow logbuddy to
 * read private fields of classes in {@code java.*} package.
 *
 * {@snippet :
 * --add-opens=java.base/java.lang=ALL-UNNAMED
 * --add-opens=java.base/java.util=ALL-UNNAMED
 * --add-opens=java.base/java.math=ALL-UNNAMED
 * --add-opens=java.base/java.lang.ref=ALL-UNNAMED
 * --add-opens=java.base/java.util.concurrent.locks=ALL-UNNAMED
 * }
 */
@SuppressWarnings("javadoc")
public class RunTests {
  public static void main(String[] args) {
    runAndReport(coreLibraryHasNoProblems());
    runAndReport(testEverything());
  }

  private static void runAndReport(Test test) {
    var report = run(test);
    var failed = filterFailed(report);
    if (count(failed) > 0) {
      err("""
          suite  : %s
          cases  : %d
          failed : %d
          """,
          nameOf(test),
          count(report),
          count(failed));
      err(format(failed));
      err(formatExceptions(failed));
      exit(1);
    } else {
      out("""
          suite  : %s
          cases  : %d
          """,
          nameOf(test),
          count(report));
    }
  }

  private static Test coreLibraryHasNoProblems() {
    return newCase("core library has no problems", () -> {
      verify(CORE_LIBRARY);
    });
  }
}
