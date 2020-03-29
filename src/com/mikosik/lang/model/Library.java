package com.mikosik.lang.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Library {
  private final Map<String, Expression> definitions = new HashMap<>();

  private Library() {}

  public static Library library() {
    return new Library();
  }

  public Library define(String name, Expression expression) {
    definitions.put(name, expression);
    return this;
  }

  public Expression get(String name) {
    return Optional.ofNullable(definitions.get(name))
        .orElseThrow(() -> new RuntimeException(name));
  }
}
