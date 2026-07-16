package com.mikosik.stork.problem;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Map;

import com.mikosik.stork.problem.compile.CannotCompile;
import com.mikosik.stork.problem.compile.importing.IllegalCharacter;
import com.mikosik.stork.problem.compile.link.DuplicatedFunction;
import com.mikosik.stork.problem.compile.link.UnboundVariable;
import com.mikosik.stork.problem.compile.link.UndefinedFunction;
import com.mikosik.stork.problem.compile.parse.UnexpectedToken;
import com.mikosik.stork.problem.compile.tokenize.IllegalCharacterInCode;
import com.mikosik.stork.problem.compile.tokenize.IllegalCharacterInString;

public class ProblemTemplates {
  private final Map<Class<?>, String> templates = new HashMap<>();

  public ProblemTemplates() {
    initializeTemplates();
  }

  private void initializeTemplates() {
    putImporterTemplates();
    putTokenizerTemplates();
    putParserTemplates();
    putLinkerTemplates();
  }

  private void putImporterTemplates() {
    put(IllegalCharacter.class,
        "import {text} contains illegal {character}");
  }

  private void putTokenizerTemplates() {
    put(IllegalCharacterInString.class,
        "string contains illegal {character}");
    put(IllegalCharacterInCode.class,
        "code contains illegal {character}");
  }

  private void putParserTemplates() {
    put(UnexpectedToken.class,
        "unexpected {token}");
  }

  private void putLinkerTemplates() {
    put(DuplicatedFunction.class,
        "function {function} is defined more than once");
    put(UndefinedFunction.class,
        "function {location} imports undefined function {undefined}");
    put(UnboundVariable.class,
        "function {location} uses undefined variable {variable}");
  }

  private void put(Class<?> type, String template) {
    templates.put(type, template);
  }

  public String get(Class<? extends CannotCompile> problem) {
    return requireNonNull(templates.get(problem));
  }

  public String get(CannotCompile problem) {
    return get(problem.getClass());
  }
}
