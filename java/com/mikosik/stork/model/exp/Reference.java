package com.mikosik.stork.model.exp;

import static com.mikosik.stork.model.exp.Namespace.root;
import static java.util.Objects.deepEquals;
import static java.util.Objects.hash;

public class Reference implements Expression {
  public final Namespace namespace;
  public final Variable variable;

  private Reference(Namespace namespace, Variable variable) {
    this.namespace = namespace;
    this.variable = variable;
  }

  public static Reference reference(Namespace namespace, Variable variable) {
    return new Reference(namespace, variable);
  }

  public static Reference reference(Variable variable) {
    return new Reference(root, variable);
  }

  public boolean equals(Object that) {
    return that instanceof Reference
        && equals((Reference) that);
  }

  private boolean equals(Reference that) {
    return deepEquals(this.namespace, that.namespace)
        && deepEquals(this.variable, that.variable);
  }

  public int hashCode() {
    return hash(namespace, variable);
  }
}
