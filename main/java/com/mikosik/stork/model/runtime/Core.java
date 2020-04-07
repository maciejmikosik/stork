package com.mikosik.stork.model.runtime;

public interface Core extends Expression {
  Expression run(Expression expression);
}
