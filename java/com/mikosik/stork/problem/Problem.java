package com.mikosik.stork.problem;

import java.util.Objects;

import com.mikosik.stork.common.Description;

public abstract class Problem extends RuntimeException {
  public abstract Description describe();

  public boolean equals(Object object) {
    return object instanceof Problem problem
        && Objects.equals(describe(), problem.describe());
  }

  public int hashCode() {
    return describe().hashCode();
  }

  public String toString() {
    return describe().toString();
  }
}
