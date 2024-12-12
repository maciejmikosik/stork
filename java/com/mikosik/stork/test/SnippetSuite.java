package com.mikosik.stork.test;

import static com.mikosik.stork.common.Throwables.runtimeException;
import static com.mikosik.stork.test.ProgramTest.programTest;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;

import org.quackery.Body;
import org.quackery.Test;

public class SnippetSuite implements Test {
  private final String name;
  private String linkage = "";
  private final List<Test> cases = new LinkedList<>();

  private SnippetSuite(String name) {
    this.name = name;
  }

  public static SnippetSuite snippetSuite(String name) {
    return new SnippetSuite(name);
  }

  public SnippetSuite importing(String link) {
    linkage = linkage + link + "\n";
    return this;
  }

  public SnippetSuite test(String snippet, Object expected) {
    cases.add(buildCase(snippet.replace('\'', '\"'), expected));
    return this;
  }

  public <R> R visit(BiFunction<String, Body, R> caseHandler,
      BiFunction<String, List<Test>, R> suiteHandler) {
    return suiteHandler.apply(name, cases);
  }

  private Test buildCase(String snippet, Object expected) {
    var type = typeOf(expected);
    return programTest(nameTemplate(type).formatted(snippet, expected))
        .importFile(linkage + extraLinkage(type))
        .sourceFile(mainTemplate(type).formatted(snippet))
        .stdout(expected.toString());
  }

  private static String nameTemplate(ExpectedType type) {
    return switch (type) {
      case STRING -> "%s = '%s'";
      default -> "%s = %s";
    };
  }

  private static String mainTemplate(ExpectedType type) {
    return switch (type) {
      case INTEGER, BOOLEAN -> "main(stdin) { format(%s) }";
      default -> "main(stdin) { %s }";
    };
  }

  private static String extraLinkage(ExpectedType type) {
    return switch (type) {
      case INTEGER -> "lang.integer.format\n";
      case BOOLEAN -> "lang.boolean.format\n";
      default -> "";
    };
  }

  private enum ExpectedType {
    INTEGER, BOOLEAN, STRING;
  }

  private static ExpectedType typeOf(Object expected) {
    return switch (expected) {
      case Integer i -> ExpectedType.INTEGER;
      case BigInteger i -> ExpectedType.INTEGER;
      case Boolean b -> ExpectedType.BOOLEAN;
      case String s -> ExpectedType.STRING;
      default -> throw runtimeException("unknown type: %s", expected);
    };
  }
}
