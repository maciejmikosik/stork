package com.mikosik.stork.model;

import static com.mikosik.stork.common.Sequence.sequenceOf;
import static com.mikosik.stork.model.Namespace.namespace;
import static com.mikosik.stork.model.Namespace.namespaceOf;
import static com.mikosik.stork.model.Variable.variable;
import static java.lang.String.join;
import static java.util.Objects.hash;

import java.util.LinkedList;
import java.util.List;

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

  public static Identifier identifier(Variable variable) {
    return new Identifier(namespaceOf(), variable);
  }

  public static Identifier identifier(String name) {
    var path = sequenceOf(name.split(SEPARATOR));
    return identifier(
        namespace(path.subList(0, path.size() - 1)),
        variable(path.getLast()));
  }

  public String name() {
    List<String> path = new LinkedList<>(namespace.path);
    path.add(variable.name);
    return join(SEPARATOR, path);
  }

  public boolean equals(Object object) {
    return object instanceof Identifier identifier
        && equals(identifier);
  }

  private boolean equals(Identifier that) {
    return namespace.equals(that.namespace)
        && variable.equals(that.variable);
  }

  public int hashCode() {
    return hash(namespace, variable);
  }

  public String toString() {
    return "Identifier(%s, %s)".formatted(namespace, variable);
  }

  private static final String SEPARATOR = "/";
}
