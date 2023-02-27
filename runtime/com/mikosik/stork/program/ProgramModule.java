package com.mikosik.stork.program;

import static com.mikosik.stork.common.Chain.chainOf;
import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.EagerInstruction.eager;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Instruction.instruction;
import static com.mikosik.stork.model.Lambda.lambda;
import static com.mikosik.stork.model.Module.module;
import static com.mikosik.stork.model.Parameter.parameter;
import static com.mikosik.stork.tool.common.Bridge.javaInteger;
import static com.mikosik.stork.tool.common.CombinatoryModule.I;

import com.mikosik.stork.common.io.Output;
import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Instruction;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Parameter;

public class ProgramModule {
  public static final Identifier WRITE_STREAM = identifier("stork.program.writeStream");
  public static final Identifier WRITE_BYTE = identifier("stork.program.writeByte");
  public static final Identifier CLOSE_STREAM = identifier("stork.program.closeStream");

  public static Module programModule(Output stdout) {
    return module(chainOf(
        definition(WRITE_STREAM, writeStream(stdout)),
        definition(WRITE_BYTE, writeByte(stdout)),
        definition(CLOSE_STREAM, CLOSE_STREAM_BODY)));
  }

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
  private static Expression writeStream(Output output) {
    Parameter stream = parameter("stream");
    Parameter head = parameter("head");
    Parameter tail = parameter("tail");
    return lambda(stream,
        application(stream,
            lambda(head, tail,
                application(WRITE_BYTE,
                    head,
                    application(WRITE_STREAM, tail))),
            CLOSE_STREAM));
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
    return eager(instruction(argument -> {
      int oneByte = javaInteger(argument).intValueExact();
      check(0 <= oneByte && oneByte <= 255);
      output.write((byte) oneByte);
      return I;
    }));
  }

  private static final Instruction CLOSE_STREAM_BODY = argument -> {
    throw new RuntimeException("not applicable");
  };
}
