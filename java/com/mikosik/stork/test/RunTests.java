package com.mikosik.stork.test;

import static com.mikosik.stork.Core.core;
import static com.mikosik.stork.Core.Mode.DEVELOPMENT;
import static com.mikosik.stork.common.Text.text;
import static com.mikosik.stork.test.MoreReports.formatExceptions;
import static com.mikosik.stork.test.QuackeryHelper.count;
import static com.mikosik.stork.test.QuackeryHelper.deep;
import static com.mikosik.stork.test.QuackeryHelper.filterFailed;
import static com.mikosik.stork.test.QuackeryHelper.ifCase;
import static com.mikosik.stork.test.QuackeryHelper.nameOf;
import static com.mikosik.stork.test.cases.everything.TestCore.testCore;
import static com.mikosik.stork.test.cases.everything.TestMainFunction.testMainFunction;
import static com.mikosik.stork.test.cases.everything.TestMathOperators.testMathOperators;
import static com.mikosik.stork.test.cases.everything.TestStdio.testStdio;
import static com.mikosik.stork.test.cases.everything.core.TestBoolean.testBoolean;
import static com.mikosik.stork.test.cases.everything.core.TestFunction.testFunction;
import static com.mikosik.stork.test.cases.everything.core.TestInteger.testInteger;
import static com.mikosik.stork.test.cases.everything.core.TestMaybe.testMaybe;
import static com.mikosik.stork.test.cases.everything.core.TestStream.testStream;
import static com.mikosik.stork.test.cases.everything.core.TestStreamCount.testStreamCount;
import static com.mikosik.stork.test.cases.language.TestImport.testImport;
import static com.mikosik.stork.test.cases.language.TestLinkerProblems.testLinkerProblems;
import static com.mikosik.stork.test.cases.language.TestSyntax.testSyntax;
import static com.mikosik.stork.test.cases.language.TestTokenizerProblems.testTokenizerProblems;
import static com.mikosik.stork.test.cases.unit.TestDecompiler.testDecompiler;
import static com.mikosik.stork.test.cases.unit.TestLogbuddyDecorator.testLogbuddyDecorator;
import static com.mikosik.stork.test.cases.unit.TestSequence.testSequence;
import static java.lang.System.exit;
import static java.time.Duration.between;
import static java.time.Duration.ofSeconds;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.quackery.report.Reports.format;
import static org.quackery.run.Runners.run;
import static org.quackery.run.Runners.timeout;

import java.time.Duration;
import java.time.Instant;

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
    runAndReport(suite("unit tests")
        .add(testSequence())
        .add(suite("debug tools")
            .add(testDecompiler())
            .add(testLogbuddyDecorator())));
    runAndReport(suite("language")
        .add(testSyntax())
        .add(testImport())
        .add(suite("compiler problems")
            .add(testTokenizerProblems())
            .add(testLinkerProblems())));
    runAndReport(compilerCanCompileCoreLibrary());
    runAndReport(suite("everything")
        .add(testMainFunction())
        .add(testStdio())
        .add(testCore())
        .add(testMathOperators())
        .add(suite("core library")
            .add(testBoolean())
            .add(testFunction())
            .add(testInteger())
            .add(testStream())
            .add(testStreamCount())
            .add(testMaybe())));
  }

  private static void runAndReport(Test test) {
    var testWithTimeout = withTimeout(test);
    var started = Instant.now();
    var report = run(testWithTimeout);
    var duration = between(started, Instant.now());
    print(duration, report);
  }

  private static Test withTimeout(Test test) {
    return deep(ifCase(caze -> timeout(ofSeconds(1), caze)))
        .apply(test);
  }

  private static void print(Duration duration, Test report) {
    var failed = filterFailed(report);
    var text = text()
        .line("suite  : ", nameOf(report))
        .line("cases  : ", count(report))
        .line("time   : ", inSeconds(duration));
    if (count(failed) > 0) {
      text
          .line("failed : ", count(failed))
          .line(format(failed))
          .line(formatExceptions(failed))
          .stderr();
      exit(1);
    } else {
      text.line().stdout();
    }
  }

  public static float inSeconds(Duration duration) {
    return duration.getSeconds() + duration.getNano() * 1E-9f;
  }

  private static Test compilerCanCompileCoreLibrary() {
    return newCase("compiler can compile core library", () -> {
      core(DEVELOPMENT);
    });
  }
}
