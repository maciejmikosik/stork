package com.mikosik.stork.common.table;

public class ReplaceIfPresent<K, V> implements Mod<K, V> {
  public final K key;
  public final V value;

  private ReplaceIfPresent(K key, V value) {
    this.key = key;
    this.value = value;
  }

  public static <K, V> ReplaceIfPresent<K, V> replaceIfPresent(K key, V value) {
    return new ReplaceIfPresent<K, V>(key, value);
  }
}
