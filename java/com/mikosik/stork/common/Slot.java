package com.mikosik.stork.common;

// TODO consistent naming E vs V
public class Slot<E> {
  public E value;

  private Slot(E value) {
    this.value = value;
  }

  public static <V> Slot<V> slot(V value) {
    return new Slot<V>(value);
  }
}
