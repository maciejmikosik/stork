package com.mikosik.stork.problem;

import java.util.Objects;

public abstract class Problem extends RuntimeException {
  public boolean equals(Object object) {
    return object instanceof Problem problem
        && Objects.equals(getMessage(), problem.getMessage());
  }

  public int hashCode() {
    return getMessage().hashCode();
  }

  public String toString() {
    return getMessage();
  }
}
