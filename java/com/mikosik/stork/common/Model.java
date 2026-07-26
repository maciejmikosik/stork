package com.mikosik.stork.common;

import static java.util.Arrays.stream;
import static java.util.Objects.deepEquals;
import static java.util.Objects.hash;

import java.lang.reflect.Field;

public abstract class Model {
  public boolean equals(Object that) {
    var type = this.getClass();
    return that != null
        && that.getClass().equals(type)
        && stream(type.getFields())
            .allMatch(field -> deepEquals(
                read(field, this),
                read(field, that)));
  }

  private static Object read(Field field, Object instance) {
    try {
      return field.get(instance);
    } catch (IllegalAccessException e) {
      throw new RuntimeException();
    }
  }

  public int hashCode() {
    return hash(stream(this.getClass().getFields())
        .map(field -> read(field, this))
        .toArray());
  }
}
