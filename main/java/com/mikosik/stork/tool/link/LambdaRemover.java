package com.mikosik.stork.tool.link;

import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Lambda.lambda;
import static com.mikosik.stork.tool.common.Combinators.B;
import static com.mikosik.stork.tool.common.Combinators.C;
import static com.mikosik.stork.tool.common.Combinators.I;
import static com.mikosik.stork.tool.common.Combinators.K;
import static com.mikosik.stork.tool.common.Combinators.S;

import com.mikosik.stork.model.Application;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Lambda;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Parameter;
import com.mikosik.stork.tool.common.Traverser;

/**
 * Transforms lambda abstractions into basis using SKI combinators.
 *
 * https://en.wikipedia.org/wiki/Combinatory_logic#Completeness_of_the_S-K_basis
 */
public class LambdaRemover implements Weaver {
  private LambdaRemover() {}

  public static LambdaRemover lambdaRemover() {
    return new LambdaRemover();
  }

  public Module weave(Module module) {
    return new Traverser() {
      protected Expression traverse(Application application) {
        // 2. T[(E₁ E₂)] => (T[E₁] T[E₂])
        return application(
            traverse(application.function),
            traverse(application.argument));
      }

      protected Expression traverse(Lambda lambda) {
        Parameter parameter = lambda.parameter;
        Expression body = lambda.body;
        if (!occurs(parameter, body)) {
          // 3. T[λx.E] => (K T[E])
          return application(K, traverse(body));
        } else if (parameter == body) {
          // 4. T[λx.x] => I
          return I;
        } else if (body instanceof Lambda) {
          // 5. T[λx.λy.E] => T[λx.T[λy.E]]
          return traverse(lambda(parameter, traverse(body)));
        } else if (body instanceof Application) {
          return traverseLambda(parameter, (Application) body);
        } else {
          throw new RuntimeException();
        }
      }

      private Expression traverseLambda(Parameter parameter, Application body) {
        boolean isInFunction = occurs(parameter, body.function);
        boolean isInArgument = occurs(parameter, body.argument);
        if (isInFunction && isInArgument) {
          // 6. T[λx.(E₁ E₂)] => (S T[λx.E₁] T[λx.E₂])
          return application(
              S,
              traverse(lambda(parameter, body.function)),
              traverse(lambda(parameter, body.argument)));
        }
        if (isInFunction && !isInArgument) {
          // 7. T[λx.(E₁ E₂)] ⇒ (C T[λx.E₁] T[E₂])
          return application(
              C,
              traverse(lambda(parameter, body.function)),
              traverse(body.argument));
        }
        if (!isInFunction && isInArgument) {
          if (body.argument == parameter) {
            // η-reduction [λx.(E x)] = T[E]
            return traverse(body.function);
          }
          // 8. T[λx.(E₁ E₂)] ⇒ (B T[E₁] T[λx.E₂])
          return application(
              B,
              traverse(body.function),
              traverse(lambda(parameter, body.argument)));
        }
        throw new RuntimeException();
      }
    }.traverse(module);
  }

  private static boolean occurs(Parameter parameter, Expression expression) {
    boolean[] result = new boolean[1];
    new Traverser() {
      protected Expression traverse(Parameter traversing) {
        if (traversing == parameter) {
          result[0] = true;
        }
        return traversing;
      }
    }.traverse(expression);
    return result[0];
  }
}
