package com.mikosik.stork.front.program;

import static com.mikosik.stork.common.Chain.chainOf;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.Lambda.lambda;
import static com.mikosik.stork.model.Module.module;
import static com.mikosik.stork.model.Parameter.parameter;
import static com.mikosik.stork.model.Variable.variable;
import static com.mikosik.stork.tool.common.Eager.eager;

import com.mikosik.stork.model.Definition;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Parameter;
import com.mikosik.stork.model.Variable;

public class StdoutModule {
  public static final Variable writeByte = variable("stork.stdout.writeByte");
  public static final Variable writeStream = variable("stork.stdout.writeStream");
  public static final Variable closeStream = variable("stork.stdout.closeStream");

  /**
   * <pre>
   *
   * writeStream(stream) {
   *   stream
   *     ((head)(tail) {
   *       ARG_1(writeByte)(head)(writeStream(tail))
   *     })
   *     (closeStream)
   *  }
   *
   * writeByte(byte)(continue) {
   *   continue
   * }
   *
   * closeStream {
   *   closeStream
   * }
   *
   * </pre>
   */
  public static Module stdoutModule() {
    return module(chainOf(
        writeStreamDefinition(),
        writeByteDefinition(),
        definition(closeStream, closeStream)));
  }

  private static Definition writeStreamDefinition() {
    Parameter stream = parameter("stream");
    Parameter head = parameter("head");
    Parameter tail = parameter("tail");
    return definition(writeStream,
        lambda(stream,
            application(stream,
                lambda(head, lambda(tail,
                    application(
                        eager(1, writeByte),
                        head,
                        application(writeStream, tail)))),
                closeStream)));
  }

  private static Definition writeByteDefinition() {
    Parameter byte_ = parameter("byte");
    Parameter continue_ = parameter("continue");
    return definition(writeByte,
        lambda(byte_, lambda(continue_, continue_)));
  }
}
