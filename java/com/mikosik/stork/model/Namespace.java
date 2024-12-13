package com.mikosik.stork.model;

import static com.mikosik.stork.common.Sequence.sequenceOf;

import com.mikosik.stork.common.Sequence;

public class Namespace {
  public final Sequence<String> path;

  private Namespace(Sequence<String> path) {
    this.path = path;
  }

  public static Namespace namespaceOf(String... path) {
    return namespace(sequenceOf(path));
  }

  public static Namespace namespace(Sequence<String> path) {
    return new Namespace(path);
  }

  public boolean equals(Object object) {
    return object instanceof Namespace namespace
        && equals(namespace);
  }

  private boolean equals(Namespace that) {
    return path.equals(that.path);
  }

  public int hashCode() {
    return path.hashCode();
  }
}
