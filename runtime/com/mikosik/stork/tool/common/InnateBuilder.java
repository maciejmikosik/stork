package com.mikosik.stork.tool.common;

import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.tool.common.Eager.eager;
import static java.util.Objects.requireNonNull;

import com.mikosik.stork.model.Computation;
import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Innate;
import com.mikosik.stork.model.Stack;

public class InnateBuilder {
  private String name;
  private int arguments = 0;
  private Innate logic;

  private InnateBuilder() {}

  public static InnateBuilder innate() {
    return new InnateBuilder();
  }

  public InnateBuilder name(String name) {
    this.name = name;
    return this;
  }

  public InnateBuilder arguments(int arguments) {
    this.arguments = arguments;
    return this;
  }

  public InnateBuilder logic(Innate logic) {
    this.logic = logic;
    return this;
  }

  public Expression build() {
    requireNonNull(name);
    requireNonNull(logic);
    return eager(arguments, innate(name, logic));
  }

  private static Innate innate(String name, Innate logic) {
    return new Innate() {
      public Computation compute(Stack stack) {
        return logic.compute(stack);
      }

      public String toString() {
        return name;
      }
    };
  }

  public Definition defineAs(String name) {
    return definition(identifier(name), build());
  }
}
