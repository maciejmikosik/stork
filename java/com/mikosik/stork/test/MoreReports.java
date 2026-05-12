package com.mikosik.stork.test;

import static com.mikosik.stork.common.Description.description;
import static com.mikosik.stork.common.Throwables.messageOf;
import static com.mikosik.stork.common.Throwables.runtimeException;
import static com.mikosik.stork.common.Throwables.stackTraceOf;
import static com.mikosik.stork.problem.Describe.describe;
import static java.lang.String.join;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.List;

import org.quackery.Body;
import org.quackery.Test;
import org.quackery.report.AssertException;

import com.mikosik.stork.problem.compile.CannotCompile;
import com.mikosik.stork.problem.compute.CannotCompute;

public class MoreReports {
  public static String format(String expectedOrActual, Outcome outcome) {
    var expectedOrActualOutcome = join(" ", expectedOrActual, nameOf(outcome));
    var stdoutOrProblem = switch (outcome.object) {
      case byte[] stdout -> description(format(stdout));
      case CannotCompile cannotCompile -> describe(cannotCompile);
      case CannotCompute cannotCompute -> describe(cannotCompute);
      default -> throw runtimeException("" + outcome.object);
    };
    var description = description(expectedOrActualOutcome)
        .child(stdoutOrProblem);
    return description.toString();
  }

  private static String format(byte[] bytes) {
    return "[" + new String(bytes, UTF_8) + "]";
  }

  private static String nameOf(Outcome outcome) {
    return switch (outcome.object) {
      case byte[] b -> "printed";
      case CannotCompile cannotCompile -> "failed";
      case CannotCompute cannotCompute -> "failed";
      default -> throw runtimeException("" + outcome.object);
    };
  }

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
      builder.append(path).append(name).append("\n\n")
          .append(messageOf(exception)).append("\n")
          .append(exception instanceof AssertException
              ? ""
              : stackTraceOf(exception))
          .repeat("-", 50).append("\n");
    }
  }

  private static void format(StringBuilder builder, String path, String name, List<Test> children) {
    for (Test child : children) {
      format(builder, path + name + " / ", child);
    }
  }
}
