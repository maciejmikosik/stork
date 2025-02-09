package com.mikosik.stork.test;

import static com.mikosik.stork.Core.core;
import static com.mikosik.stork.Core.Mode.DEVELOPMENT;
import static com.mikosik.stork.common.StandardOutput.err;
import static com.mikosik.stork.common.StandardOutput.out;
import static com.mikosik.stork.test.MoreReports.formatExceptions;
import static com.mikosik.stork.test.QuackeryHelper.count;
import static com.mikosik.stork.test.QuackeryHelper.deep;
import static com.mikosik.stork.test.QuackeryHelper.filterFailed;
import static com.mikosik.stork.test.QuackeryHelper.ifCase;
import static com.mikosik.stork.test.QuackeryHelper.ignore;
import static com.mikosik.stork.test.QuackeryHelper.nameOf;
import static com.mikosik.stork.test.cases.TestComputers.testComputers;
import static com.mikosik.stork.test.cases.TestCoreLibrary.testCoreLibrary;
import static com.mikosik.stork.test.cases.TestDecompiler.testDecompiler;
import static com.mikosik.stork.test.cases.TestInstructions.testInstructions;
import static com.mikosik.stork.test.cases.TestLogbuddyDecorator.testLogbuddyDecorator;
import static com.mikosik.stork.test.cases.TestParseOptions.testParseOptions;
import static com.mikosik.stork.test.cases.TestSequence.testSequence;
import static com.mikosik.stork.test.cases.TestSimplePrograms.testSimplePrograms;
import static com.mikosik.stork.test.cases.language.TestLanguage.testLanguage;
import static java.lang.System.exit;
import static java.time.Duration.between;
import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.quackery.report.Reports.format;
import static org.quackery.run.Runners.run;
import static org.quackery.run.Runners.timeout;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.time.Duration;
import java.time.Instant;

import org.quackery.Test;

// TODO why @snippet gives warning? Must be suppressed for now.

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
        .add(testComputers())
        .add(testParseOptions())
        .add(suite("debug tools")
            .add(testDecompiler())
            .add(testLogbuddyDecorator())));
    runAndReport(testLanguage());
    runAndReport(compilerCanCompileCoreLibrary());
    runAndReport(coreLibraryIsSerializable());
    runAndReport(suite("programs")
        .add(testSimplePrograms())
        .add(ignore(testInstructions()))
        .add(testCoreLibrary()));
  }

  private static void runAndReport(Test test) {
    var testWithTimeout = withTimeout(test);
    var started = Instant.now();
    var report = run(testWithTimeout);
    var duration = between(started, Instant.now());
    print(duration, report);
  }

  private static Test withTimeout(Test test) {
    return deep(ifCase(RunTests::withTimeoutCase))
        .apply(test);
  }

  private static Test withTimeoutCase(Test caze) {
    return nameOf(caze).contains("logbuddy")
        ? timeout(ofSeconds(1), caze)
        : timeout(ofMillis(100), caze);
  }

  private static void print(Duration duration, Test report) {
    var failed = filterFailed(report);
    if (count(failed) > 0) {
      err("""
          suite  : %s
          cases  : %d
          time   : %.3fs
          failed : %d
          """,
          nameOf(report),
          count(report),
          inSeconds(duration),
          count(failed));
      err(format(failed));
      err(formatExceptions(failed));
      exit(1);
    } else {
      out("""
          suite  : %s
          cases  : %d
          time   : %.3fs
          """,
          nameOf(report),
          count(report),
          inSeconds(duration));
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

  private static Test coreLibraryIsSerializable() {
    return newCase("core library is serializable", () -> {
      var coreLibrary = core(DEVELOPMENT);
      var output = new ObjectOutputStream(new ByteArrayOutputStream());
      output.writeObject(coreLibrary);
      output.close();
    });
  }
}
