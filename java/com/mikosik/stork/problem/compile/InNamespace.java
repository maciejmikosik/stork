package com.mikosik.stork.problem.compile;

import com.mikosik.stork.model.Namespace;

public class InNamespace extends CannotCompile {
  public final Namespace namespace;
  public final CannotCompile cause;

  private InNamespace(
      Namespace namespace,
      CannotCompile cause) {
    this.namespace = namespace;
    this.cause = cause;
  }

  public static InNamespace in(
      Namespace namespace,
      CannotCompile cause) {
    return new InNamespace(namespace, cause);
  }
}
