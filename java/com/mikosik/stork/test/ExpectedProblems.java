package com.mikosik.stork.test;

import static com.mikosik.stork.test.QuackeryHelper.assertException;

import com.mikosik.stork.problem.Problem;

public class ExpectedProblems {
  private Problem expected = null;

  private ExpectedProblems() {}

  public static ExpectedProblems expectedProblems() {
    return new ExpectedProblems();
  }

  public void expect(Problem problem) {
    expected = problem;
  }

  public void verify(Problem actual) {
    var actualDescription = actual.description();
    if (expected == null) {
      throw assertException("unexpected problem\n\n" + actualDescription);
    }
    var expectedDescription = expected.description();
    if (!expectedDescription.equals(actualDescription)) {
      throw assertException(
          "expected problem\n\n%s\nbut found problem\n\n%s"
              .formatted(expectedDescription, actualDescription));
    }
  }

  public void verify() {
    if (expected != null) {
      var expectedDescription = expected.description();
      throw assertException(
          "expected\n\n%s\nbut found no problem"
              .formatted(expectedDescription));
    }
  }
}
