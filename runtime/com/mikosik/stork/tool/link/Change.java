package com.mikosik.stork.tool.link;

import java.util.function.Function;

import com.mikosik.stork.model.Expression;

public interface Change<E> extends Function<E, Expression> {}
