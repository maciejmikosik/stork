package com.mikosik.stork.program;

import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Eager.eager;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Lambda.lambda;
import static com.mikosik.stork.model.Parameter.parameter;
import static com.mikosik.stork.tool.common.Combinator.I;
import static com.mikosik.stork.tool.common.Combinator.Y;
import static com.mikosik.stork.tool.common.Instructions.instruction;
import static com.mikosik.stork.tool.common.Instructions.name;

import java.math.BigInteger;

import com.mikosik.stork.common.io.Output;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Instruction;
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
   *     (closeStream)
   * })
   * </pre>
   */
  public static Expression writeStream(Output output) {
    Parameter self = parameter("self");
    Parameter stream = parameter("stream");
    Parameter head = parameter("head");
    Parameter tail = parameter("tail");
    return application(Y,
        lambda(self, stream,
            application(stream,
                lambda(head, tail,
                    application(writeByte(output),
                        head,
                        application(self, tail))),
                CLOSE_STREAM)));
  }

  public static final Instruction CLOSE_STREAM = name(
      identifier("closeStream"),
      argument -> Stdout.CLOSE_STREAM);

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
  public static Expression writeByte(Output output) {
    return eager(name(identifier("writeByte"), instruction(argument -> {
      int oneByte = toJavaInteger(argument).intValueExact();
      check(0 <= oneByte && oneByte <= 255);
      output.write((byte) oneByte);
      return I;
    })));
  }

  private static BigInteger toJavaInteger(Expression expression) {
    return ((Integer) expression).value;
  }
}
