package com.mikosik.stork.lib;

import static com.mikosik.stork.common.Chain.chainOf;
import static com.mikosik.stork.model.def.Definition.definition;
import static com.mikosik.stork.model.def.Library.library;
import static com.mikosik.stork.model.runtime.Primitive.primitive;
import static com.mikosik.stork.model.runtime.Variable.variable;
import static java.lang.String.format;

import java.math.BigInteger;

import com.mikosik.stork.model.def.Definition;
import com.mikosik.stork.model.def.Library;
import com.mikosik.stork.model.runtime.Core;
import com.mikosik.stork.model.runtime.Expression;
import com.mikosik.stork.model.runtime.Primitive;

class CoreLibrary {
  public static Library coreLibrary() {
    return library(chainOf(
        addIntegerInteger(),
        negateInteger(),
        equalIntegerInteger(),
        moreThanIntegerInteger()));
  }

  private static Definition addIntegerInteger() {
    return definition("add", new Core() {
      public Expression run(Expression argumentA) {
        BigInteger numberA = (BigInteger) ((Primitive) argumentA).object;
        return new Core() {
          public Expression run(Expression argumentB) {
            BigInteger numberB = (BigInteger) ((Primitive) argumentB).object;
            return primitive(numberA.add(numberB));
          }

          public String toString() {
            return format("add(%s)", numberA);
          }
        };
      }

      public String toString() {
        return "add";
      }
    });
  }

  private static Definition negateInteger() {
    return definition("negate", new Core() {
      public Expression run(Expression argument) {
        BigInteger bigInteger = (BigInteger) ((Primitive) argument).object;
        return primitive(bigInteger.negate());
      }

      public String toString() {
        return "negate";
      }
    });
  }

  private static Definition equalIntegerInteger() {
    return definition("equal", new Core() {
      public Expression run(Expression argumentA) {
        BigInteger numberA = (BigInteger) ((Primitive) argumentA).object;
        return new Core() {
          public Expression run(Expression argumentB) {
            BigInteger numberB = (BigInteger) ((Primitive) argumentB).object;
            return variable("" + numberA.equals(numberB));
          }

          public String toString() {
            return format("equal(%s)", numberA);
          }
        };
      }

      public String toString() {
        return "equal";
      }
    });
  }

  private static Definition moreThanIntegerInteger() {
    return definition("moreThan", new Core() {
      public Expression run(Expression argumentA) {
        BigInteger numberA = (BigInteger) ((Primitive) argumentA).object;
        return new Core() {
          public Expression run(Expression argumentB) {
            BigInteger numberB = (BigInteger) ((Primitive) argumentB).object;
            return variable("" + (numberB.compareTo(numberA) > 0));
          }

          public String toString() {
            return format("moreThan(%s)", numberA);
          }
        };
      }

      public String toString() {
        return "moreThan";
      }
    });
  }
}
