package com.mikosik.stork.common;

public class Slot<E> {
  public E value;

  private Slot(E value) {
    this.value = value;
  }

  public static <E> Slot<E> slot(E value) {
    return new Slot<E>(value);
  }
}
