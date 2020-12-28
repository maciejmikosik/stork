package com.mikosik.stork.tool.common;

import static com.mikosik.stork.model.Variable.variable;

import com.mikosik.stork.model.Expression;

public class Translate {
  public static Expression asStorkBoolean(boolean bool) {
    return variable("stork.boolean." + Boolean.toString(bool));
  }
}
