package com.mikosik.stork.model;

import static com.mikosik.stork.common.ImmutableList.join;
import static com.mikosik.stork.common.ImmutableList.none;
import static com.mikosik.stork.common.ImmutableList.single;

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

  public boolean equals(Object object) {
    return object instanceof Namespace namespace
        && equals(namespace);
  }

  private boolean equals(Namespace that) {
    return components.equals(that.components);
  }

  public int hashCode() {
    return components.hashCode();
  }
}
