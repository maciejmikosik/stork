package com.mikosik.stork.debug;

import static com.mikosik.stork.common.io.Serializables.ascii;
import static com.mikosik.stork.compile.link.Bind.removeNamespaces;
import static com.mikosik.stork.compute.Computation.computation;
import static com.mikosik.stork.compute.Computations.abort;
import static com.mikosik.stork.debug.Decompiler.decompile;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Identifier.identifier;
import static java.lang.String.format;

import java.util.Map;
import java.util.WeakHashMap;

import org.logbuddy.renderer.TextRenderer;

import com.mikosik.stork.compute.Computation;
import com.mikosik.stork.compute.Computer;
import com.mikosik.stork.compute.Stack;
import com.mikosik.stork.model.Expression;

public final class StorkTextRenderer extends TextRenderer {
  private final Map<Stack, Integer> stackDepth = new WeakHashMap<>();

  protected StorkTextRenderer() {}

  public String render(Object model) {
    if (model instanceof Computer computer) {
      return render(computer);
    } else if (model instanceof Computation computation) {
      return render(computation);
    } else if (model instanceof Expression expression) {
      return render(expression);
    } else if (model instanceof Stack stack) {
      return render(stack);
    } else if (model instanceof Map) {
      return "Map";
    } else {
      return super.render(model);
    }
  }

  private String render(Computer computer) {
    return computer.getClass().getSimpleName();
  }

  private String render(Computation computation) {
    return render(asExpression(computation));
  }

  private String render(Expression expression) {
    return ascii(decompile(removeNamespaces(expression)));
  }

  private String render(Stack stack) {
    return format("stack(%s)", depthOf(stack));
  }

  private int depthOf(Stack stack) {
    return stackDepth.computeIfAbsent(stack, this::computeDepthOf);
  }

  private int computeDepthOf(Stack stack) {
    return stack.isEmpty()
        ? 0
        : depthOf(stack.pop()) + 1;
  }

  private static Expression asExpression(Computation computation) {
    return abort(mark(computation));
  }

  private static Computation mark(Computation computation) {
    return computation(
        application(identifier("@"), computation.expression),
        computation.stack);
  }
}
