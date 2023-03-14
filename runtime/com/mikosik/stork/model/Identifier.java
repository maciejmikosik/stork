package com.mikosik.stork.model;

import static com.mikosik.stork.common.Sequence.sequence;
import static com.mikosik.stork.model.Namespace.namespace;
import static com.mikosik.stork.model.Variable.variable;
import static java.lang.String.join;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Identifier implements Expression {
  public final Namespace namespace;
  public final Variable variable;

  private Identifier(Namespace namespace, Variable variable) {
    this.namespace = namespace;
    this.variable = variable;
  }

  public static Identifier identifier(Namespace namespace, Variable variable) {
    return new Identifier(namespace, variable);
  }

  public static Identifier identifier(String name) {
    List<String> path = sequence(name.split("\\."));
    return identifier(
        namespace(path.subList(0, path.size() - 1)),
        variable(path.get(path.size() - 1)));
  }

  public String name() {
    List<String> path = new LinkedList<>(namespace.path);
    path.add(variable.name);
    return join(".", path);
  }

  public boolean equals(Object that) {
    return that instanceof Identifier identifier
        && equals(identifier);
  }

  private boolean equals(Identifier that) {
    return namespace.path.equals(that.namespace.path)
        && variable.name.equals(that.variable.name);
  }

  public int hashCode() {
    return Objects.hash(
        namespace.path,
        variable.name);
  }

  public String toString() {
    return name();
  }
}
