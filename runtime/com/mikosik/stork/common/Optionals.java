package com.mikosik.stork.common;

import static java.util.Arrays.stream;

import java.util.Optional;
import java.util.function.Supplier;

public class Optionals {
  public static <T> Optional<T> maybeFirstPresent(Supplier<Optional<T>>... optionals) {
    return stream(optionals)
        .map(Supplier::get)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst();
  }
}
