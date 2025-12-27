package com.mikosik.stork.model;

import static com.mikosik.stork.common.ImmutableList.join;
import static com.mikosik.stork.common.ImmutableList.single;
import static com.mikosik.stork.common.ImmutableList.toList;

import java.util.List;

public class Namespace {
  public final List<String> path;

  private Namespace(List<String> path) {
    this.path = path;
  }

  public static Namespace namespaceOf(String... path) {
    return namespace(toList(path));
  }

  public static Namespace namespace(List<String> path) {
    return new Namespace(path);
  }

  public Namespace add(String component) {
    return namespace(join(path, single(component)));
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
