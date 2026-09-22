package com.mikosik.stork.problem.compute;

import static com.mikosik.stork.common.text.Outline.outline;

import com.mikosik.stork.common.text.Outline;

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

  public Outline toOutline() {
    return outline("CannotCompute");
  }
}
