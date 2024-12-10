package com.mikosik.stork.common;

import static com.mikosik.stork.common.Throwables.fail;

public class Reserver {
  private Object type;

  private Reserver() {}

  public static Reserver reserver() {
    return new Reserver();
  }

  public void reserve(Object type) {
    if (!isLegal(type)) {
      fail("'%s' -> '%s'".formatted(this.type, type));
    }
    this.type = type;
  }

  private boolean isLegal(Object type) {
    return type != null && (this.type == null || this.type == type);
  }
}
