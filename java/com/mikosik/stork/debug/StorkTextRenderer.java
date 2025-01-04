package com.mikosik.stork.debug;

import static com.mikosik.stork.common.io.Serializables.ascii;
import static com.mikosik.stork.compile.link.Bind.removeNamespaces;
import static com.mikosik.stork.compute.Computation.computation;
import static com.mikosik.stork.compute.Computations.abort;
import static com.mikosik.stork.compute.Computations.depthOf;
import static com.mikosik.stork.debug.Decompiler.decompile;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Identifier.identifier;
import static java.lang.String.format;

import java.util.Map;

import org.logbuddy.renderer.TextRenderer;

import com.mikosik.stork.compute.Computation;
import com.mikosik.stork.compute.Computer;
import com.mikosik.stork.compute.Stack;
import com.mikosik.stork.model.Expression;

// TODO Decompiler is tested but renderer is not.
public final class StorkTextRenderer extends TextRenderer {
  protected StorkTextRenderer() {}

  public String render(Object model) {
    return switch (model) {
      case Computer computer -> computer.getClass().getSimpleName();
      case Computation computation -> render(asExpression(computation));
      case Expression expression -> ascii(decompile(removeNamespaces(expression)));
      case Stack stack -> format("stack(%s)", depthOf(stack));
      case Map<?, ?> map -> "Map";
      default -> super.render(model);
    };
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
