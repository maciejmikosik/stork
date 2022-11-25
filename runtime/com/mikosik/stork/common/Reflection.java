package com.mikosik.stork.common;

import static java.util.Arrays.stream;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Predicate;

public class Reflection {
  public static <T> Optional<T> maybeRead(Predicate<Field> predicate, Object instance) {
    return (Optional<T>) stream(instance.getClass().getDeclaredFields())
        .filter(predicate)
        .map(field -> accessible(field))
        .map(field -> read(field, instance))
        .findFirst();
  }

  public static <T> Optional<T> maybeReadOfType(Class<T> type, Object instance) {
    return maybeRead(isOfType(type), instance);
  }

  public static Predicate<Field> hasSignature(Class<?> type, String name) {
    return field -> field.getType() == type
        && field.getName().equals(name);
  }

  public static Predicate<Field> isOfType(Class<?> type) {
    return field -> field.getType() == type;
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
