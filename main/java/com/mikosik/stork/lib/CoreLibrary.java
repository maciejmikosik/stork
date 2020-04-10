package com.mikosik.stork.lib;

import static com.mikosik.stork.common.Chain.chainOf;
import static com.mikosik.stork.model.def.Definition.definition;
import static com.mikosik.stork.model.def.Library.library;
import static com.mikosik.stork.model.runtime.Primitive.primitive;
import static com.mikosik.stork.model.runtime.Variable.variable;
import static java.lang.String.format;

import java.math.BigInteger;

import com.mikosik.stork.model.def.Library;
import com.mikosik.stork.model.runtime.Core;
import com.mikosik.stork.model.runtime.Expression;
import com.mikosik.stork.model.runtime.Primitive;
import com.mikosik.stork.tool.Runner;

class CoreLibrary {
  public static Library coreLibrary() {
    return library(chainOf(
        definition("add", addIntegerInteger()),
        definition("negate", negateInteger()),
        definition("equal", equalIntegerInteger()),
        definition("moreThan", moreThanIntegerInteger())));
  }

  private static RecursiveCore addIntegerInteger() {
    return new RecursiveCore() {
      public Expression run(Expression argumentA) {
        BigInteger numberA = (BigInteger) ((Primitive) argumentA).object;
        return new RecursiveCore() {
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
    };
  }

  private static RecursiveCore negateInteger() {
    return new RecursiveCore() {
      public Expression run(Expression argument) {
        BigInteger bigInteger = (BigInteger) ((Primitive) argument).object;
        return primitive(bigInteger.negate());
      }

      public String toString() {
        return "negate";
      }
    };
  }

  private static RecursiveCore equalIntegerInteger() {
    return new RecursiveCore() {
      public Expression run(Expression argumentA) {
        BigInteger numberA = (BigInteger) ((Primitive) argumentA).object;
        return new RecursiveCore() {
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
    };
  }

  private static RecursiveCore moreThanIntegerInteger() {
    return new RecursiveCore() {
      public Expression run(Expression argumentA) {
        BigInteger numberA = (BigInteger) ((Primitive) argumentA).object;
        return new RecursiveCore() {
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
    };
  }

  private static abstract class RecursiveCore implements Core {
    public final Expression run(Expression argument, Runner runner) {
      Expression runArgument = runner.run(argument);
      Expression result = run(runArgument);
      Expression runResult = runner.run(result);
      return runResult;
    }

    protected abstract Expression run(Expression argument);
  }
}
