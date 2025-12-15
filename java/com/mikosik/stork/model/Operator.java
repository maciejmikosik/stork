package com.mikosik.stork.model;

import com.mikosik.stork.compute.Computation;
import com.mikosik.stork.compute.Stack;

public interface Operator extends Expression {
  Computation compute(Stack stack);
}
