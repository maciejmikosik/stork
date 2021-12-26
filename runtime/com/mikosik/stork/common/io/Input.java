package com.mikosik.stork.common.io;

import static com.mikosik.stork.common.io.Buffer.newBuffer;
import static com.mikosik.stork.common.io.InputOutput.unchecked;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.newInputStream;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
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

  public static Input input(Path file) {
    try {
      return input(newInputStream(file));
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  public static Input tryInput(Path file) {
    return exists(file)
        ? input(file)
        : input(new ByteArrayInputStream(new byte[0]));
  }

  public int read() {
    try {
      return input.read();
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
    int oneByte;
    while ((oneByte = peek()) != -1 && predicate.test(oneByte)) {
      output.write(read());
    }
    return buffer.toBlob();
  }

  public Input pumpTo(Output output) {
    int oneByte;
    while ((oneByte = read()) != -1) {
      output.write(oneByte);
    }
    return this;
  }

  public Input pumpToAndFlush(Output output) {
    pumpTo(output);
    output.flush();
    return this;
  }

  public int peek() {
    try {
      input.mark(1);
      int oneByte = read();
      input.reset();
      return oneByte;
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  public Scanner scan(Charset charset) {
    return new Scanner(input, charset.name());
  }
}
