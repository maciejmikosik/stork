package com.mikosik.stork.program;

import static com.mikosik.stork.common.Throwables.check;
import static com.mikosik.stork.compile.link.Bridge.REDUCE_EAGER;
import static com.mikosik.stork.compile.link.StackOperator.EAGER;
import static com.mikosik.stork.compute.Computation.computation;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.problem.compute.CannotCompute.cannotCompute;

import java.util.Optional;

import com.mikosik.stork.common.io.Output;
import com.mikosik.stork.compute.Computation;
import com.mikosik.stork.compute.Stack;
import com.mikosik.stork.compute.Stack.Argument;
import com.mikosik.stork.compute.Stack.Function;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Integer;
import com.mikosik.stork.model.Operator;

// TODO why @snippet gives warning?.
@SuppressWarnings("javadoc")
public class Stdout {
  /**
   * {@snippet :
   *   writeStream {
   *     reduceEager(close)(eager(writeByte))
   *   }
   * }
   */
  public static Expression writeStreamTo(Output output) {
    return application(
        REDUCE_EAGER,
        CLOSE,
        application(EAGER, writeByteTo(output)));
  }

  public static Expression writeByteTo(Output output) {
    return new Operator() {
      public Optional<Computation> compute(Stack stack) {
        int nArguments = 2;
        var arguments = new Expression[nArguments];
        for (int iArgument = 0; iArgument < nArguments; iArgument++) {
          if (stack instanceof Argument argument) {
            arguments[iArgument] = argument.expression;
            stack = argument.previous;
          } else {
            throw cannotCompute();
          }
        }
        var integer = ((Integer) arguments[0]);
        int oneByte = integer.value.intValueExact();
        check(0 <= oneByte && oneByte <= 255);
        output.write((byte) oneByte);
        return Optional.of(computation(arguments[1], stack));
      }

      public String toString() {
        return "WRITE";
      }
    };
  }

  public static final Expression CLOSE = new Operator() {
    public Optional<Computation> compute(Stack stack) {
      return switch (stack) {
        case Function function -> Optional.of(
            computation(
                application(function.expression, this),
                function.previous));
        default -> Optional.empty();
      };
    }

    public String toString() {
      return "CLOSE";
    }
  };
}
