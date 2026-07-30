package com.mikosik.stork.common;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeToken<T> {
  private final Type type;

  protected TypeToken() {
    var genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
    this.type = genericSuperclass.getActualTypeArguments()[0];
  }

  public Type getType() {
    return type;
  }

  public Class<?> getRawClass() {
    return classFromType(type);
  }

  public static Class<?> classFromType(Type type_) {
    return switch (type_) {
      case Class<?> type -> type;
      case ParameterizedType type -> (Class<?>) type.getRawType();
      case GenericArrayType type -> Array.newInstance(
          classFromType(type.getGenericComponentType()),
          0)
          .getClass();
      default -> throw new RuntimeException();
    };
  }
}
