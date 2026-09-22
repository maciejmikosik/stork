package com.mikosik.stork.model.exp;

import static com.mikosik.stork.common.col.ImmutableList.join;
import static com.mikosik.stork.common.col.ImmutableList.none;
import static com.mikosik.stork.common.col.ImmutableList.single;
import static java.util.Objects.deepEquals;
import static java.util.Objects.hash;

import java.util.List;

public class Namespace {
  public final List<String> components;

  private Namespace(List<String> components) {
    this.components = components;
  }

  public static Namespace namespaceRoot() {
    return namespace(none());
  }

  public static Namespace namespace(List<String> components) {
    return new Namespace(components);
  }

  public Namespace add(String component) {
    return namespace(join(components, single(component)));
  }

  public boolean equals(Object that) {
    return that instanceof Namespace namespace
        && equals(namespace);
  }

  private boolean equals(Namespace that) {
    return deepEquals(this.components, that.components);
  }

  public int hashCode() {
    return hash(components);
  }
}
