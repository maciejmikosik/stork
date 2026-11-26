package com.mikosik.stork.compile;

import static com.mikosik.stork.common.io.Buffer.newBuffer;
import static com.mikosik.stork.compile.PhraseOutput.phrase;
import static java.nio.charset.StandardCharsets.US_ASCII;

import com.mikosik.stork.model.exp.Phrase;

public class Decompiler {
  public static String decompile(Phrase phrase) {
    var buffer = newBuffer();
    phrase(buffer.asOutput()).write(phrase);
    return new String(buffer.bytes(), US_ASCII);
  }
}
