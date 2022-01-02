package com.mikosik.stork.program;

import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Combinator.Y;
import static com.mikosik.stork.model.Computation.computation;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Lambda.lambda;
import static com.mikosik.stork.model.Parameter.parameter;
import static com.mikosik.stork.tool.common.InnateBuilder.innate;

import com.mikosik.stork.common.io.Output;
import com.mikosik.stork.model.Expression;
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
    return application(Y, lambda(self, lambda(stream,
        application(stream,
            lambda(head, lambda(tail,
                application(
                    writeByte(output),
                    head,
                    application(self, tail)))),
            closeStream(output)))));
  }

  /**
   * <pre>
   * writeByte(byte)(continue) {
   *   # imperatively write byte to output
   *   continue
   * }
   * </pre>
   */
  private static Expression writeByte(Output output) {
    return innate()
        .name("$writeByte")
        .logic(stack -> {
          int oneByte = stack
              .argumentIntegerJava()
              .intValueExact();
          check(0 <= oneByte && oneByte <= 255);
          output.write((byte) oneByte);
          return computation(
              stack.pop().argument(),
              stack.pop().pop());
        })
        // first argument is eager
        // second argument is lazy
        .arguments(1)
        .build();
  }

  /**
   * <pre>
   * closeStream {
   *   # imperatively close output
   *   streamClosed
   * }
   * </pre>
   */
  private static Expression closeStream(Output output) {
    return innate()
        .name("$closeStream")
        .logic(stack -> {
          output.close();
          return computation(identifier("$streamClosed"));
        })
        .build();
  }
}
