package com.mikosik.lang.model.runtime;

public interface Core extends Expression {
  Expression run(Expression expression);
}
