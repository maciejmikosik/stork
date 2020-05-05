package com.mikosik.stork.testing;

import static java.util.stream.Collectors.toList;
import static org.quackery.Suite.suite;
import static org.quackery.help.Helpers.thrownBy;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Optional;

import org.quackery.Body;
import org.quackery.Test;
import org.quackery.report.AssertException;

public class MoreReports {
  public static String formatExceptions(Test report) {
    StringBuilder builder = new StringBuilder();
    format(builder, "", report);
    return builder.toString();
  }

  private static void format(StringBuilder builder, String path, Test report) {
    report.visit(
        (name, body) -> {
          format(builder, path, name, body);
          return null;
        },
        (name, children) -> {
          format(builder, path, name, children);
          return null;
        });
  }

  private static void format(StringBuilder builder, String path, String name, Body body) {
    try {
      body.run();
    } catch (Throwable exception) {
      builder.append(path).append(name).append("\n");
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

  private static void format(StringBuilder builder, String path, String name, List<Test> children) {
    for (Test child : children) {
      format(builder, path + name + " / ", child);
    }
  }

  private static final String SEPARATOR = repeat(50, '-') + "\n";

  private static String repeat(int times, char character) {
    return new String(new char[times]).replace((char) 0, character);
  }

  public static Optional<Test> filter(Class<? extends Throwable> type, Test test) {
    return test.visit(
        (name, body) -> thrownBy(body)
            .flatMap(throwable -> type.isInstance(throwable)
                ? Optional.of(throwable)
                : Optional.empty())
            .map(throwable -> test),
        (name, children) -> filter(type, name, children));
  }

  private static Optional<Test> filter(
      Class<? extends Throwable> type,
      String name,
      List<Test> children) {
    List<Test> filteredChildren = children.stream()
        .map(child -> filter(type, child))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(toList());
    return filteredChildren.isEmpty()
        ? Optional.empty()
        : Optional.of(suite(name).addAll(filteredChildren));
  }
}
