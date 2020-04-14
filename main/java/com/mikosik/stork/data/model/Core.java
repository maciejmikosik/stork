package com.mikosik.stork.data.model;

public interface Core extends Expression {
  Expression run(Expression argument);
}
