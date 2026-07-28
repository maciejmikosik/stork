package com.mikosik.stork.common;

import static com.mikosik.stork.common.Throwables.runtimeException;

import java.util.Arrays;

public class Objects {
  public static int deepHash(Object object) {
    return object == null
        ? 0
        : object.getClass().isArray()
            ? deepHashArray(object)
            : object.hashCode();
  }

  private static int deepHashArray(Object object) {
    var type = object.getClass();
    if (type == Object[].class) {
      return Arrays.hashCode((Object[]) object);
    } else if (type == byte[].class) {
      return Arrays.hashCode((byte[]) object);
    } else if (type == char[].class) {
      return Arrays.hashCode((char[]) object);
    } else if (type == short[].class) {
      return Arrays.hashCode((short[]) object);
    } else if (type == int[].class) {
      return Arrays.hashCode((int[]) object);
    } else if (type == long[].class) {
      return Arrays.hashCode((long[]) object);
    } else if (type == float[].class) {
      return Arrays.hashCode((float[]) object);
    } else if (type == double[].class) {
      return Arrays.hashCode((double[]) object);
    } else if (type == boolean[].class) {
      return Arrays.hashCode((boolean[]) object);
    } else {
      throw runtimeException("impossible");
    }
  }
}
