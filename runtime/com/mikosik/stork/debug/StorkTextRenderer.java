package com.mikosik.stork.debug;

import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Computation.computation;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.tool.common.Computations.abort;
import static java.lang.String.format;

import java.util.Map;
import java.util.WeakHashMap;

import org.logbuddy.renderer.TextRenderer;

import com.mikosik.stork.model.Computation;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Stack;
import com.mikosik.stork.tool.compute.Computer;
import com.mikosik.stork.tool.decompile.Decompiler;

public final class StorkTextRenderer extends TextRenderer {
  private final Map<Stack, Integer> stackDepth = new WeakHashMap<>();
  private final Decompiler decompiler;

  protected StorkTextRenderer(Decompiler decompiler) {
    this.decompiler = decompiler;
  }

  public String render(Object model) {
    if (model instanceof Computer) {
      return render((Computer) model);
    } else if (model instanceof Computation) {
      return render((Computation) model);
    } else if (model instanceof Expression) {
      return render((Expression) model);
    } else if (model instanceof Stack) {
      return render((Stack) model);
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
    return decompiler.decompile(expression);
  }

  private String render(Stack stack) {
    return format("stack(%s)", depthOf(stack));
  }

  private int depthOf(Stack stack) {
    return stackDepth.computeIfAbsent(stack, this::computeDepthOf);
  }

  private int computeDepthOf(Stack stack) {
    // TODO stack.isEmpty()
    return stack.hasArgument() || stack.hasFunction()
        ? depthOf(stack.pop()) + 1
        : 0;
  }

  private static Expression asExpression(Computation computation) {
    return abort(mark(computation));
  }

  private static Computation mark(Computation computation) {
    return computation(
        application(variable("@"), computation.expression),
        computation.stack);
  }
}
