package com.mikosik.stork.compile.err;

import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.deepEquals;
import static java.util.Objects.hash;

import java.util.LinkedHashMap;
import java.util.Map;

import com.mikosik.stork.model.exp.Namespace;
import com.mikosik.stork.model.exp.Reference;
import com.mikosik.stork.model.exp.Variable;

public class Miscompilation {
  public final String name;
  public final Map<String, Object> attributes;

  private Miscompilation(String name, Map<String, Object> attributes) {
    this.name = name;
    this.attributes = attributes;
  }

  public static Builder miscompilation(String name) {
    return new Builder(name, new LinkedHashMap<>());
  }

  public Builder edit() {
    return new Builder(name, new LinkedHashMap<>(attributes));
  }

  public boolean equals(Object that) {
    return that instanceof Miscompilation miscompilation
        && equals(miscompilation);
  }

  private boolean equals(Miscompilation that) {
    return deepEquals(this.name, that.name)
        && deepEquals(this.attributes, that.attributes);
  }

  public int hashCode() {
    return hash(name, attributes);
  }

  public static class Builder {
    private final String name;
    private final Map<String, Object> attributes;

    private Builder(String name, Map<String, Object> attributes) {
      this.name = name;
      this.attributes = attributes;
    }

    public Builder set(String key, Object value) {
      attributes.put(key, value);
      return this;
    }

    public Builder in(Namespace namespace) {
      return set("inNamespace", namespace);
    }

    public Builder in(Variable variable) {
      return set("inFunction", variable);
    }

    public Builder in(Reference reference) {
      return in(reference.namespace)
          .in(reference.variable);
    }

    public Miscompilation build() {
      return new Miscompilation(name, unmodifiableMap(attributes));
    }
  }
}
