package com.mikosik.lang;

import static com.mikosik.lang.model.runtime.Primitive.primitive;
import static com.mikosik.lang.model.runtime.Variable.variable;
import static java.lang.String.format;

import java.math.BigInteger;

import com.mikosik.lang.model.def.Library;
import com.mikosik.lang.model.runtime.Core;
import com.mikosik.lang.model.runtime.Expression;
import com.mikosik.lang.model.runtime.Primitive;

public class CoreLibrary {
  public static void installCore(Library library) {
    library.define("add", new Core() {
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
    library.define("negate", new Core() {
      public Expression run(Expression argument) {
        BigInteger bigInteger = (BigInteger) ((Primitive) argument).object;
        return primitive(bigInteger.negate());
      }

      public String toString() {
        return "negate";
      }
    });
    library.define("equal", new Core() {
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
    library.define("moreThan", new Core() {
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
