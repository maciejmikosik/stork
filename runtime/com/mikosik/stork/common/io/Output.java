package com.mikosik.stork.common.io;

import static com.mikosik.stork.common.io.InputOutput.unchecked;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class Output implements AutoCloseable {
  private final OutputStream output;

  private Output(OutputStream output) {
    this.output = output;
  }

  public static Output output(OutputStream output) {
    return new Output(output);
  }

  public void write(byte b) {
    try {
      output.write(b);
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  public void flush() {
    try {
      output.flush();
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  public void close() {
    try {
      output.close();
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  public PrintStream asPrintStream(Charset charset) {
    try {
      return new PrintStream(output, false, charset.name());
    } catch (UnsupportedEncodingException e) {
      throw unchecked(e);
    }
  }
}
