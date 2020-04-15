package com.mikosik.stork.data.model;

public interface Core extends Expression {
  Expression apply(Expression argument);
}
