package com.mikosik.stork.common.table;

import static com.mikosik.stork.common.Check.check;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import com.mikosik.stork.common.Chain;

public class Table<K, V> {
  private final Map<K, V> map;

  private Table(Map<K, V> map) {
    this.map = map;
  }

  public static <K, V> Table<K, V> table(Chain<Mod<K, V>> mods) {
    Map<K, V> map = new HashMap<>();
    for (Mod<K, V> mod : mods) {
      if (mod instanceof Put) {
        execute((Put<K, V>) mod, map);
      } else if (mod instanceof ReplaceIfPresent) {
        execute((ReplaceIfPresent<K, V>) mod, map);
      } else {
        throw new RuntimeException();
      }
    }
    return new Table<>(map);
  }

  private static <K, V> void execute(Put<K, V> mod, Map<K, V> map) {
    check(!map.containsKey(mod.key));
    map.put(mod.key, mod.value);
  }

  private static <K, V> void execute(ReplaceIfPresent<K, V> mod, Map<K, V> map) {
    if (map.containsKey(mod.key)) {
      map.put(mod.key, mod.value);
    }
  }

  public V get(K key) {
    if (map.containsKey(key)) {
      return map.get(key);
    } else {
      throw new NoSuchElementException("" + key);
    }
  }
}
