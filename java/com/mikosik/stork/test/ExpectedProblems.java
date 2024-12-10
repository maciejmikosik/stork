package com.mikosik.stork.test;

import static com.mikosik.stork.common.Collections.intersection;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toCollection;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.quackery.report.AssertException;

import com.mikosik.stork.problem.Problem;

public class ExpectedProblems {
  private final List<Problem> expected = new LinkedList<>();

  private ExpectedProblems() {}

  public static ExpectedProblems expectedProblems() {
    return new ExpectedProblems();
  }

  public void expect(Problem problem) {
    expected.add(problem);
  }

  public void verify(List<? extends Problem> actual) {
    verify(descriptions(actual), descriptions(expected));
  }

  public void verify() {
    verify(emptyList());
  }

  private static void verify(Set<String> actual, Set<String> expected) {
    var common = intersection(actual, expected);
    actual.removeAll(common);
    expected.removeAll(common);
    if (!expected.isEmpty() || !actual.isEmpty()) {
      throw new AssertException(formatMessage(actual, expected));
    }
  }

  private static Set<String> descriptions(List<? extends Problem> problems) {
    return problems.stream()
        .map(Problem::description)
        .collect(toCollection(HashSet::new));
  }

  private static String formatMessage(Set<String> actual, Set<String> expected) {
    var builder = new StringBuilder();
    append(builder, "expected", expected);
    append(builder, "not expected", actual);
    return builder.toString();
  }

  private static void append(
      StringBuilder builder,
      String headline,
      Set<String> problems) {
    if (!problems.isEmpty()) {
      builder.append(headline).append("\n\n");
      for (String problem : problems) {
        builder.append(problem).append("\n");
      }
    }
  }
}
