package com.mikosik.stork.problem.compute;

public class CannotCompute {
  public static CannotCompute cannotCompute() {
    return new CannotCompute();
  }

  public boolean equals(Object that) {
    return that != null
        && that.getClass() == this.getClass();
  }

  public int hashCode() {
    return 123;
  }
}
