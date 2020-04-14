package com.mikosik.stork.common.table;

public class Put<K, V> implements Mod<K, V> {
  public final K key;
  public final V value;

  private Put(K key, V value) {
    this.key = key;
    this.value = value;
  }

  public static <K, V> Put<K, V> put(K key, V value) {
    return new Put<K, V>(key, value);
  }
}
