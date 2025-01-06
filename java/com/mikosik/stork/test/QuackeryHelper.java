package com.mikosik.stork.test;

import static org.quackery.Suite.suite;
import static org.quackery.help.Helpers.successfulCase;
import static org.quackery.help.Helpers.thrownBy;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import org.quackery.Body;
import org.quackery.Test;
import org.quackery.report.AssertException;

// TODO implement those functions in quackery
public class QuackeryHelper {
  public static boolean isCase(Test test) {
    return test.visit(
        (name, body) -> true,
        (name, children) -> false);
  }

  public static boolean isSuite(Test test) {
    return !isCase(test);
  }

  public static Function<Test, Test> ifCase(BiFunction<String, Body, Test> change) {
    return test -> test.visit(
        change::apply,
        (name, children) -> test);
  }

  public static Function<Test, Test> ifSuite(BiFunction<String, List<Test>, Test> change) {
    return test -> test.visit(
        (name, body) -> test,
        change::apply);
  }

  public static Function<Test, Test> filter(Predicate<Test> predicate) {
    return ifSuite((name, children) -> suite(name)
        .addAll(children.stream()
            .filter(predicate)
            .toList()));
  }

  public static Function<Test, Test> deep(Function<Test, Test> change) {
    return test -> test.visit(
        (name, body) -> change.apply(test),
        (name, children) -> change.apply(suite(name)
            .addAll(children.stream()
                .map(deep(change))
                .toList())));
  }

  public static Test filterFailed(Test test) {
    return test.visit(
        (caseName, caseBody) -> thrownBy(caseBody).isPresent()
            ? test
            : suite(caseName),
        (suiteName, suiteChildren) -> deep(filter(t -> t.visit(
            (name, body) -> thrownBy(body).isPresent(),
            (name, children) -> !children.isEmpty())))
                .apply(test));
  }

  public static int count(Test test) {
    return test.visit(
        (name, body) -> 1,
        (name, children) -> children.stream()
            .mapToInt(QuackeryHelper::count)
            .sum());
  }

  public static String nameOf(Test test) {
    return test.visit(
        (name, body) -> name,
        (name, children) -> name);
  }

  public static void assertSame(Object actual, Object expected) {
    if (actual != expected) {
      throw assertException("""
          expected
            %s
          but was
            %s
          """,
          expected,
          actual);
    }
  }

  public static AssertException assertException(String format, Object... args) {
    return new AssertException(format.formatted(args));
  }

  public static Test ignore(Test test) {
    return deep(ifCase((name, body) -> successfulCase("[ignored] " + name)))
        .apply(test);
  }
}
