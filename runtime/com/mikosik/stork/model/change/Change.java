package com.mikosik.stork.model.change;

import java.util.function.Function;

import com.mikosik.stork.model.Expression;

public interface Change<E> extends Function<E, Expression> {}
