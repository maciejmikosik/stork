package com.mikosik.stork.common;

import static java.util.Arrays.stream;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Predicate;

public class Reflection {
  public static Optional<Object> read(Predicate<Field> predicate, Object instance) {
    return stream(instance.getClass().getDeclaredFields())
        .filter(predicate::test)
        .map(field -> accessible(field))
        .map(field -> read(field, instance))
        .findFirst();
  }

  public static Predicate<Field> signature(Class<?> type, String name) {
    return field -> field.getType() == type
        && field.getName().equals(name);
  }

  private static Field accessible(Field field) {
    field.setAccessible(true);
    return field;
  }

  private static Object read(Field field, Object instance) {
    try {
      return field.get(instance);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
