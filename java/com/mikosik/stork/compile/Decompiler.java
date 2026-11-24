package com.mikosik.stork.compile;

import static com.mikosik.stork.common.io.Buffer.newBuffer;
import static com.mikosik.stork.compile.PhraseOutput.phrase;
import static java.nio.charset.StandardCharsets.US_ASCII;

import com.mikosik.stork.model.exp.Definition;
import com.mikosik.stork.model.exp.Expression;

public class Decompiler {
  public static String decompile(Definition definition) {
    var buffer = newBuffer();
    phrase(buffer.asOutput()).write(definition);
    return new String(buffer.bytes(), US_ASCII);
  }

  public static String decompile(Expression expression) {
    var buffer = newBuffer();
    phrase(buffer.asOutput()).write(expression);
    return new String(buffer.bytes(), US_ASCII);
  }
}
