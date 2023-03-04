package com.mikosik.stork.compile;

import static com.mikosik.stork.common.Chain.chain;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Instruction.instruction;
import static com.mikosik.stork.model.Module.module;
import static com.mikosik.stork.model.Namespace.namespace;
import static com.mikosik.stork.model.Variable.variable;

import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Namespace;

public class CombinatoryModule {
  public static final Namespace NAMESPACE = namespace(chain("combinator", "native", "lang"));

  public static final Identifier I = id("i");
  public static final Identifier Y = id("y");
  public static final Identifier K = id("k");
  public static final Identifier S = id("s");
  public static final Identifier C = id("c");
  public static final Identifier B = id("b");

  private static Identifier id(String name) {
    return identifier(NAMESPACE, variable(name));
  }

  public static Module combinatoryModule() {
    return module(chain(
        /** I(x) = x */
        definition(I, instruction(x -> x)),

        /** Y(x) = x(Y(x)) */
        definition(Y, instruction(x -> ap(x, ap(Y, x)))),

        /** K(x)(y) = x */
        definition(K, instruction((x, y) -> x)),

        /** S(x)(y)(z) = x(z)(y(z)) */
        definition(S, instruction((x, y, z) -> ap(ap(x, z), ap(y, z)))),

        /** C(x)(y)(z) = x(z)(y) */
        definition(C, instruction((x, y, z) -> ap(ap(x, z), y))),

        /** B(x)(y)(z) = x(y(z)) */
        definition(B, instruction((x, y, z) -> ap(x, ap(y, z))))));
  }

  private static Expression ap(Expression function, Expression argument) {
    return application(function, argument);
  }
}
