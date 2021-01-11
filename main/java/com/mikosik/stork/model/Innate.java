package com.mikosik.stork.model;

public interface Innate extends Expression {
  Computation compute(Stack stack);
}
