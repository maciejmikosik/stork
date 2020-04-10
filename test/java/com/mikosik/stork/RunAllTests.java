package com.mikosik.stork;

import static com.mikosik.stork.MoreReports.formatExceptions;
import static com.mikosik.stork.TestBooleanLibrary.testBooleanLibrary;
import static com.mikosik.stork.TestFunctionLibrary.testFunctionLibrary;
import static com.mikosik.stork.TestOptionalLibrary.testOptionalLibrary;
import static com.mikosik.stork.TestSimpleFunctions.testSimpleFunctions;
import static org.quackery.Suite.suite;
import static org.quackery.report.Reports.format;
import static org.quackery.run.Runners.run;

import org.quackery.Suite;
import org.quackery.Test;

public class RunAllTests {
  public static void main(String[] args) {
    Suite test = suite("test basics")
        .add(testSimpleFunctions())
        .add(testFunctionLibrary())
        .add(testBooleanLibrary())
        .add(testOptionalLibrary());
    Test report = run(test);

    System.out.println(formatExceptions(report));
    System.out.println(format(report));
  }
}
