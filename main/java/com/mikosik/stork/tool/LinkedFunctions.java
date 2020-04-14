package com.mikosik.stork.tool;

import static com.mikosik.stork.data.model.Primitive.primitive;
import static com.mikosik.stork.data.model.Variable.variable;
import static java.lang.String.format;

import java.math.BigInteger;

import com.mikosik.stork.data.model.Core;
import com.mikosik.stork.data.model.Expression;
import com.mikosik.stork.data.model.Primitive;

class LinkedFunctions {
  public static RecursiveCore addIntegerInteger() {
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

  public static RecursiveCore negateInteger() {
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

  public static RecursiveCore equalIntegerInteger() {
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

  public static RecursiveCore moreThanIntegerInteger() {
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
