package com.mikosik.stork.model;

import static com.mikosik.stork.model.Namespace.namespace;
import static com.mikosik.stork.model.Variable.variable;
import static java.lang.String.join;

import java.util.Objects;

import com.mikosik.stork.common.Chain;

public class Identifier implements Expression {
  public final Namespace namespace;
  public final Variable variable;

  private Identifier(Namespace namespace, Variable variable) {
    this.namespace = namespace;
    this.variable = variable;
  }

  public static Identifier identifier(String name) {
    Chain<String> path = Chain.<String> chain()
        .addAll(name.split("\\."));
    return new Identifier(
        namespace(path.tail()),
        variable(path.head()));
  }

  public String name() {
    return join(".", namespace.path.add(variable.name).reverse().toLinkedList());
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
