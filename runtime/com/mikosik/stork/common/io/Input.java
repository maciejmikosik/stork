package com.mikosik.stork.common.io;

import static com.mikosik.stork.common.io.Buffer.newBuffer;
import static com.mikosik.stork.common.io.InputOutput.unchecked;
import static com.mikosik.stork.common.io.MaybeByte.maybeByte;
import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.function.IntPredicate;

public class Input implements AutoCloseable {
  private final InputStream input;

  private Input(InputStream input) {
    this.input = input;
  }

  public static Input input(InputStream input) {
    return new Input(input);
  }

  public static Input input(byte[] bytes) {
    return input(new ByteArrayInputStream(bytes));
  }

  public static Input input(String string) {
    return input(string.getBytes(US_ASCII));
  }

  public MaybeByte read() {
    try {
      return maybeByte(input.read());
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  public void close() {
    try {
      input.close();
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  public Input buffered() {
    return input(new BufferedInputStream(input));
  }

  public Blob readAllBytes() {
    Buffer buffer = newBuffer();
    pumpTo(buffer.asOutput());
    return buffer.toBlob();
  }

  public Blob readAllBytes(IntPredicate predicate) {
    Buffer buffer = newBuffer();
    Output output = buffer.asOutput();
    MaybeByte maybeByte;
    while ((maybeByte = peek()).hasByte() && predicate.test(maybeByte.getByte())) {
      output.write(read().getByte());
    }
    return buffer.toBlob();
  }

  public Input pumpTo(Output output) {
    MaybeByte maybeByte;
    while ((maybeByte = read()).hasByte()) {
      output.write(maybeByte.getByte());
    }
    return this;
  }

  public Input pumpToAndFlush(Output output) {
    pumpTo(output);
    output.flush();
    return this;
  }

  public MaybeByte peek() {
    try {
      input.mark(1);
      MaybeByte maybeByte = read();
      input.reset();
      return maybeByte;
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  public Scanner scan(Charset charset) {
    return new Scanner(input, charset.name());
  }
}
