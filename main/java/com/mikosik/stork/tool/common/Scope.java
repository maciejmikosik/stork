package com.mikosik.stork.tool.common;

import com.mikosik.stork.data.model.Variable;

public enum Scope {
  GLOBAL, LOCAL;

  public String format(Variable variable) {
    switch (this) {
      case GLOBAL:
        return variable.name;
      case LOCAL:
        return localize(variable.name);
      default:
        throw new RuntimeException();
    }
  }

  private static String localize(String string) {
    return string.substring(string.lastIndexOf('.') + 1);
  }
}
