package com.mikosik.stork.compile.err;

import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.deepEquals;
import static java.util.Objects.hash;

import java.util.LinkedHashMap;
import java.util.Map;

import com.mikosik.stork.model.exp.Identifier;
import com.mikosik.stork.model.exp.Namespace;
import com.mikosik.stork.model.exp.Variable;

public class Problem {
  public final String name;
  public final Map<String, Object> description;

  private Problem(String name, Map<String, Object> description) {
    this.name = name;
    this.description = description;
  }

  public static ProblemBuilder problem(String name) {
    return new ProblemBuilder(name, new LinkedHashMap<>());
  }

  public ProblemBuilder edit() {
    return new ProblemBuilder(name, new LinkedHashMap<>(description));
  }

  public boolean equals(Object that) {
    return that instanceof Problem problem
        && equals(problem);
  }

  private boolean equals(Problem that) {
    return deepEquals(this.name, that.name)
        && deepEquals(this.description, that.description);
  }

  public int hashCode() {
    return hash(name, description);
  }

  public static class ProblemBuilder {
    private final String name;
    private final Map<String, Object> description;

    private ProblemBuilder(String name, Map<String, Object> description) {
      this.name = name;
      this.description = description;
    }

    private ProblemBuilder describe(String key, Object value) {
      description.put(key, value);
      return this;
    }

    public ProblemBuilder location(Namespace namespace) {
      return describe("location", namespace);
    }

    public ProblemBuilder location(Identifier identifier) {
      return describe("location", identifier);
    }

    public ProblemBuilder object(Object object) {
      return describe("object", object);
    }

    public ProblemBuilder character(byte character) {
      return describe("character", character);
    }

    public ProblemBuilder variable(Variable variable) {
      return describe("variable", variable);
    }

    public Problem build() {
      return new Problem(name, unmodifiableMap(description));
    }
  }
}
