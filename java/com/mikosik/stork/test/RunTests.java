package com.mikosik.stork.test;

import static com.mikosik.stork.Project.project;
import static com.mikosik.stork.common.StandardOutput.err;
import static com.mikosik.stork.common.StandardOutput.out;
import static com.mikosik.stork.compile.Compilation.compilation;
import static com.mikosik.stork.compile.Compiler.compile;
import static com.mikosik.stork.compile.Compiler.nativeLibrary;
import static com.mikosik.stork.test.MoreReports.formatExceptions;
import static com.mikosik.stork.test.QuackeryHelper.count;
import static com.mikosik.stork.test.QuackeryHelper.filterFailed;
import static com.mikosik.stork.test.QuackeryHelper.ignore;
import static com.mikosik.stork.test.QuackeryHelper.nameOf;
import static com.mikosik.stork.test.cases.TestCompilerProblems.testCompilerProblems;
import static com.mikosik.stork.test.cases.TestComputers.testComputers;
import static com.mikosik.stork.test.cases.TestCoreLibrary.testCoreLibrary;
import static com.mikosik.stork.test.cases.TestDecompiler.testDecompiler;
import static com.mikosik.stork.test.cases.TestInstructions.testInstructions;
import static com.mikosik.stork.test.cases.TestLogbuddyDecorator.testLogbuddyDecorator;
import static com.mikosik.stork.test.cases.TestSequence.testSequence;
import static com.mikosik.stork.test.cases.TestSimplePrograms.testSimplePrograms;
import static java.lang.System.exit;
import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.quackery.report.Reports.format;
import static org.quackery.run.Runners.run;
import static org.quackery.run.Runners.timeout;

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
        .add(fast(testSequence()))
        .add(fast(testComputers()))
        .add(suite("debug tools")
            .add(fast(testDecompiler()))
            .add(slow(testLogbuddyDecorator()))));
    runAndReport(slow(compilerCanCompileCoreLibrary()));
    runAndReport(fast(suite("programs")
        .add(testSimplePrograms())
        .add(testCompilerProblems())
        .add(ignore(testInstructions()))
        .add(testCoreLibrary())));
  }

  private static Test fast(Test test) {
    return timeout(ofMillis(100), test);
  }

  private static Test slow(Test test) {
    return timeout(ofSeconds(3), test);
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

  private static Test compilerCanCompileCoreLibrary() {
    return newCase("compiler can compile core library", () -> {
      var project = project();
      compile(compilation()
          .source(project.coreLibraryDirectory)
          .library(nativeLibrary()));
    });
  }
}
