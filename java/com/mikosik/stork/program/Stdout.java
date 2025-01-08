package com.mikosik.stork.program;

import static com.mikosik.stork.common.Throwables.check;
import static com.mikosik.stork.compile.link.Bridge.REDUCE_EAGER;
import static com.mikosik.stork.compile.link.Combinator.I;
import static com.mikosik.stork.compile.link.StackOperator.EAGER;
import static com.mikosik.stork.compute.Computation.computation;
import static com.mikosik.stork.model.Application.application;

import java.util.Optional;

import com.mikosik.stork.common.io.Output;
import com.mikosik.stork.compute.Computation;
import com.mikosik.stork.compute.Stack;
import com.mikosik.stork.compute.Stack.Argument;
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
        if (stack instanceof Argument argumentA
            && argumentA.expression instanceof Integer integer
            && argumentA.previous instanceof Argument argumentB) {
          int oneByte = integer.value.intValueExact();
          check(0 <= oneByte && oneByte <= 255);
          output.write((byte) oneByte);
          return Optional.of(computation(I, argumentB));
        } else {
          return Optional.empty();
        }
      }

      public String toString() {
        return "WRITE";
      }
    };
  }

  public static final Expression CLOSE = new Operator() {
    public Optional<Computation> compute(Stack stack) {
      return Optional.empty();
    }

    public String toString() {
      return "CLOSE";
    }
  };
}
