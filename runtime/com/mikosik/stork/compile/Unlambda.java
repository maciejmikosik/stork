package com.mikosik.stork.compile;

import static com.mikosik.stork.compile.CombinatoryModule.B;
import static com.mikosik.stork.compile.CombinatoryModule.C;
import static com.mikosik.stork.compile.CombinatoryModule.I;
import static com.mikosik.stork.compile.CombinatoryModule.K;
import static com.mikosik.stork.compile.CombinatoryModule.S;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.change.Changes.deep;
import static com.mikosik.stork.model.change.Changes.ifLambda;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import com.mikosik.stork.model.Application;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Lambda;
import com.mikosik.stork.model.Parameter;

/**
 * Transforms lambda abstractions into basis using SKI combinators.
 *
 * https://en.wikipedia.org/wiki/Combinatory_logic#Completeness_of_the_S-K_basis
 */
public class Unlambda {
  // 2. T[(E₁ E₂)] => (T[E₁] T[E₂])
  // 5. T[λx.λy.E] => T[λx.T[λy.E]]
  public static final Function<Expression, Expression> unlambda = ifLambda(Unlambda::transform);

  private static Expression transform(Lambda lambda) {
    return transform(lambda.parameter, lambda.body);
  }

  private static Expression transform(Parameter parameter, Expression body) {
    if (!occurs(parameter, body)) {
      // 3. T[λx.E] => (K T[E])
      return application(K, body);
    } else if (parameter == body) {
      // 4. T[λx.x] => I
      return I;
    } else if (body instanceof Application application) {
      return transform(parameter, application);
    } else {
      throw new RuntimeException();
    }
  }

  private static Expression transform(Parameter parameter, Application body) {
    boolean isInFunction = occurs(parameter, body.function);
    boolean isInArgument = occurs(parameter, body.argument);
    if (isInFunction && isInArgument) {
      // 6. T[λx.(E₁ E₂)] => (S T[λx.E₁] T[λx.E₂])
      return application(S, transform(parameter, body.function),
          transform(parameter, body.argument));
    }
    if (isInFunction && !isInArgument) {
      // 7. T[λx.(E₁ E₂)] ⇒ (C T[λx.E₁] T[E₂])
      return application(C, transform(parameter, body.function), body.argument);
    }
    if (!isInFunction && isInArgument) {
      if (body.argument == parameter) {
        // η-reduction [λx.(E x)] = T[E]
        return body.function;
      }
      // 8. T[λx.(E₁ E₂)] ⇒ (B T[E₁] T[λx.E₂])
      return application(B, body.function, transform(parameter, body.argument));
    }
    throw new RuntimeException();
  }

  private static boolean occurs(Parameter parameter, Expression expression) {
    AtomicBoolean result = new AtomicBoolean(false);
    deep(traversing -> {
      if (traversing == parameter) {
        result.set(true);
      }
      return traversing;
    }).apply(expression);
    return result.get();
  }
}
