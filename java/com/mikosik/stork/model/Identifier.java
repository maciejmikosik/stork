package com.mikosik.stork.model;

import static com.mikosik.stork.model.Namespace.namespaceRoot;

import com.mikosik.stork.common.Model;

public class Identifier extends Model implements Expression {
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
}
