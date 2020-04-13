package com.mikosik.stork.testing;

import static java.util.stream.Collectors.toList;
import static org.quackery.Suite.suite;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Optional;

import org.quackery.Case;
import org.quackery.Suite;
import org.quackery.Test;
import org.quackery.report.AssertException;

public class MoreReports {
  public static String formatExceptions(Test report) {
    StringBuilder builder = new StringBuilder();
    format(builder, "", report);
    return builder.toString();
  }

  private static void format(StringBuilder builder, String path, Test report) {
    if (report instanceof Case) {
      format(builder, path, (Case) report);
    } else {
      format(builder, path, (Suite) report);
    }
  }

  private static void format(StringBuilder builder, String path, Case report) {
    try {
      report.run();
    } catch (Throwable exception) {
      builder.append(path).append(report.name).append("\n");
      builder.append("\n");
      if (exception instanceof AssertException) {
        builder.append(exception.getMessage());
      } else {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        exception.printStackTrace(new PrintStream(buffer));
        builder.append(buffer.toString());
      }
      builder.append(SEPARATOR);
    }
  }

  private static void format(StringBuilder builder, String path, Suite report) {
    for (Test child : report.tests) {
      format(builder, path + report.name + " / ", child);
    }
  }

  private static final String SEPARATOR = repeat(50, '-') + "\n";

  private static String repeat(int times, char character) {
    return new String(new char[times]).replace((char) 0, character);
  }

  public static Optional<Test> filter(Class<? extends Throwable> type, Test test) {
    return test instanceof Case
        ? filter(type, (Case) test)
        : filter(type, (Suite) test);
  }

  public static Optional<Test> filter(Class<? extends Throwable> type, Case test) {
    return isThrowing(test)
        ? Optional.of(test)
        : Optional.empty();
  }

  public static Optional<Test> filter(Class<? extends Throwable> type, Suite test) {
    List<Test> filteredSubtests = test.tests.stream()
        .map(subtest -> filter(type, subtest))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(toList());
    return filteredSubtests.isEmpty()
        ? Optional.empty()
        : Optional.of(suite(test.name).addAll(filteredSubtests));
  }

  private static boolean isThrowing(Case test) {
    try {
      test.run();
      return false;
    } catch (Throwable e) {
      return true;
    }
  }
}
