package com.mikosik.stork.program;

import static com.mikosik.stork.build.link.Bridge.instruction;
import static com.mikosik.stork.build.link.Bridge.javaInteger;
import static com.mikosik.stork.build.link.CombinatoryModule.I;
import static com.mikosik.stork.common.Check.check;
import static com.mikosik.stork.common.Sequence.sequence;
import static com.mikosik.stork.model.Application.application;
import static com.mikosik.stork.model.Definition.definition;
import static com.mikosik.stork.model.EagerInstruction.eager;
import static com.mikosik.stork.model.Identifier.identifier;
import static com.mikosik.stork.model.Lambda.lambda;
import static com.mikosik.stork.model.Module.module;
import static com.mikosik.stork.model.Namespace.namespace;
import static com.mikosik.stork.model.Parameter.parameter;
import static com.mikosik.stork.model.Variable.variable;

import com.mikosik.stork.model.Expression;
import com.mikosik.stork.model.Identifier;
import com.mikosik.stork.model.Module;
import com.mikosik.stork.model.Namespace;
import com.mikosik.stork.model.Parameter;

@SuppressWarnings("javadoc")
public class ProgramModule {
  public static final Namespace NAMESPACE = namespace(sequence("lang", "native", "program"));

  public static final Identifier WRITE_STREAM = id("writeStream");
  public static final Identifier WRITE_BYTE = id("writeByte");
  public static final Identifier CLOSE_STREAM = id("closeStream");

  private static Identifier id(String name) {
    return identifier(NAMESPACE, variable(name));
  }

  public static Module programModule() {
    return module(sequence(
        definition(WRITE_STREAM, writeStream()),
        definition(WRITE_BYTE, writeByte()),
        definition(CLOSE_STREAM, instruction(argument -> {
          throw new RuntimeException("not applicable");
        }))));
  }

  /**
   * classical version
   *
   * {@snippet :
   *   writeStream(output)(stream) {
   *     stream
   *       ((head)(tail){ writeByte(output)(head)(writeStream(output)(tail)) })
   *       (closeStream)
   *   }
   * }
   *
   * Y-recursive version
   *
   * {@snippet :
   *   writeStream = Y((self)(output)(stream) {
   *     stream
   *       ((head)(tail){ writeByte(output)(head)(self(output)(tail)) })
   *       (closeStream)
   *   })
   * }
   */
  private static Expression writeStream() {
    Parameter output = parameter("output");
    Parameter stream = parameter("stream");
    Parameter head = parameter("head");
    Parameter tail = parameter("tail");
    return lambda(output, stream,
        application(stream,
            lambda(head, tail,
                application(WRITE_BYTE,
                    output,
                    head,
                    application(WRITE_STREAM, output, tail))),
            CLOSE_STREAM));
  }

  /**
   * classical version
   *
   * {@snippet :
   *   writeByte(output)(byte)(continue) {
   *     # imperatively write byte to output
   *     continue
   *   }
   * }
   *
   * instruction version
   *
   * {@snippet :
   *   writeByte(output)(byte) {
   *     # imperatively write byte to output
   *     I
   *   }
   * }
   */
  private static Expression writeByte() {
    return eager(instruction((argStdout, argByte) -> {
      Stdout stdout = (Stdout) argStdout;
      int oneByte = javaInteger(argByte).intValueExact();
      check(0 <= oneByte && oneByte <= 255);
      stdout.output.write((byte) oneByte);
      return I;
    }));
  }
}
