package com.mikosik.stork.program;

import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Eager.eager;
import static com.mikosik.stork.model.Lambda.lambda;
import static com.mikosik.stork.model.Parameter.parameter;
import static com.mikosik.stork.tool.common.Constants.END_OF_STREAM;
import static com.mikosik.stork.tool.common.Constants.I;
import static com.mikosik.stork.tool.common.Constants.Y;
import static com.mikosik.stork.tool.common.Instructions.instruction1;

import java.math.BigInteger;

import com.mikosik.stork.common.io.Output;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Integer;
import com.mikosik.stork.model.Parameter;

public class Stdout {
  /**
   * classical version
   *
   * <pre>
   * writeStream(stream) {
   *   stream
   *     ((head)(tail){ writeByte(head)(writeStream(tail)) })
   *     (closeStream)
   * }
   * </pre>
   *
   * Y-recursive version
   *
   * <pre>
   * writeStream = Y((self)(stream) {
   *   stream
   *     ((head)(tail){ writeByte(head)(self(tail)) })
   *     (END_OF_STREAM)
   * })
   * </pre>
   */
  public static Expression writeStream(Output output) {
    Parameter self = parameter("self");
    Parameter stream = parameter("stream");
    Parameter head = parameter("head");
    Parameter tail = parameter("tail");
    return application(Y, lambda(self, lambda(stream,
        application(stream,
            lambda(head, lambda(tail,
                application(
                    writeByte(output),
                    head,
                    application(self, tail)))),
            END_OF_STREAM))));
  }

  /**
   * classical version
   *
   * <pre>
   * writeByte(byte)(continue) {
   *   # imperatively write byte to output
   *   continue
   * }
   * </pre>
   *
   * instruction version
   *
   * <pre>
   * writeByte(byte) {
   *   # imperatively write byte to output
   *   I
   * }
   * </pre>
   */
  private static Expression writeByte(Output output) {
    // TODO name writeByte
    return eager(instruction1(argument -> {
      int oneByte = toJavaInteger(argument).intValueExact();
      check(0 <= oneByte && oneByte <= 255);
      output.write((byte) oneByte);
      return I;
    }));
  }

  private static BigInteger toJavaInteger(Expression expression) {
    return ((Integer) expression).value;
  }
}
