package com.mikosik.stork.common.func;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class Functions {
  public static interface Fab<A, B> extends Function<A, B> {}

  public static interface Fabc<A, B, C> extends BiFunction<A, B, C> {}

  public static interface Faa<A> extends UnaryOperator<A>, Fab<A, A> {}

  public static interface Faaa<A> extends BinaryOperator<A>, Fabc<A, A, A> {}
}
