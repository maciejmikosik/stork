package com.mikosik.stork.common;

import java.lang.reflect.Field;

public class Reflection {
  public static Object read(Field field, Object instance) {
    try {
      return field.get(instance);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
