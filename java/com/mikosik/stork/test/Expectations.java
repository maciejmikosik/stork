package com.mikosik.stork.test;

import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.common.Collections.intersection;
import static com.mikosik.stork.common.io.Ascii.ascii;
import static com.mikosik.stork.test.Expectations.Type.CANNOT_COMPILE;
import static com.mikosik.stork.test.Expectations.Type.CANNOT_COMPUTE;
import static com.mikosik.stork.test.Expectations.Type.STDOUT;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toCollection;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.quackery.report.AssertException;

import com.mikosik.stork.problem.Problem;
import com.mikosik.stork.problem.compile.CannotCompile;
import com.mikosik.stork.problem.compute.CannotCompute;

public class Expectations {
  private Type type = null;
  private final List<Problem> problems = new LinkedList<>();
  private byte[] stdout;

  private Expectations() {}

  public static Expectations expectations() {
    return new Expectations();
  }

  public void expect(Problem problem) {
    expect(typeOf(problem));
    problems.add(problem);
  }

  private void expect(Type type) {
    check(this.type == null || this.type == type);
    this.type = type;
  }

  public void stdout(byte[] stdout) {
    check(type == null);
    type = STDOUT;
    this.stdout = stdout;
  }

  public void actualCompileProblems(List<? extends Problem> actualProblems) {
    actualProblems(CANNOT_COMPILE, actualProblems);
  }

  public void actualComputeProblems(List<? extends Problem> actualProblems) {
    actualProblems(CANNOT_COMPUTE, actualProblems);
  }

  private void actualProblems(Type type, List<? extends Problem> actualProblems) {
    var actual = descriptions(actualProblems);
    var expected = descriptions(expectedProblems(type));
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

  private List<Problem> expectedProblems(Type type) {
    return this.type == type
        ? problems
        : emptyList();
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

  public void actualStdout(byte[] actualStdout) {
    if (!Arrays.equals(actualStdout, stdout)) {
      throw new AssertException(format(""
          + "expected output\n"
          + "  %s\n"
          + "but was\n"
          + "  %s\n",
          ascii(stdout),
          ascii(actualStdout)));
    }
  }

  private Type typeOf(Problem problem) {
    if (problem instanceof CannotCompile) {
      return CANNOT_COMPILE;
    } else if (problem instanceof CannotCompute) {
      return CANNOT_COMPUTE;
    } else {
      throw new RuntimeException();
    }
  }

  enum Type {
    CANNOT_COMPILE, CANNOT_COMPUTE, STDOUT
  }
}
