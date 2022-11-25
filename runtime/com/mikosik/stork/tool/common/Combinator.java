package com.mikosik.stork.tool.common;

import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.tool.common.Instructions.instruction;

import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Instruction;

public class Combinator {
  /** I(x) = x */
  public static final Instruction I = name("I", instruction(x -> x));

  /** Y(x) = x(Y(x)) */
  public static final Instruction Y = name("Y", x -> ap(x, ap(Combinator.Y, x)));

  /** K(x)(y) = x */
  public static final Instruction K = name("K", instruction((x, y) -> x));

  /** S(x)(y)(z) = x(z)(y(z)) */
  public static final Instruction S = name("S", instruction((x, y, z) -> ap(ap(x, z), ap(y, z))));

  /** C(x)(y)(z) = x(z)(y) */
  public static final Instruction C = name("C", instruction((x, y, z) -> ap(ap(x, z), y)));

  /** B(x)(y)(z) = x(y(z)) */
  public static final Instruction B = name("B", instruction((x, y, z) -> ap(x, ap(y, z))));

  private static Expression ap(Expression function, Expression argument) {
    return application(function, argument);
  }

  private static Instruction name(String name, Instruction instruction) {
    return Instructions.name(identifier(name), instruction);
  }
}
