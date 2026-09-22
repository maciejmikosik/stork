package com.mikosik.stork.model.exp;

import static com.mikosik.stork.model.exp.Namespace.namespaceRoot;
import static java.util.Objects.deepEquals;
import static java.util.Objects.hash;

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
    return new Identifier(namespaceRoot(), variable);
  }

  public boolean equals(Object that) {
    return that instanceof Identifier identifier
        && equals(identifier);
  }

  private boolean equals(Identifier that) {
    return deepEquals(this.namespace, that.namespace)
        && deepEquals(this.variable, that.variable);
  }

  public int hashCode() {
    return hash(namespace, variable);
  }
}
