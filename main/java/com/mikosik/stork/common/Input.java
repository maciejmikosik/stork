package com.mikosik.stork.common;

import static com.mikosik.stork.common.InputOutput.unchecked;
import static com.mikosik.stork.common.Output.output;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.newInputStream;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Scanner;

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

  public static Input resource(Class<?> clas, String name) {
    return input(clas.getResourceAsStream(name));
  }

  public int read() {
    try {
      return input.read();
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  public int read(byte[] b) {
    try {
      return input.read(b);
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  public int read(byte[] b, int off, int len) {
    try {
      return input.read(b, off, len);
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  public long skip(long n) {
    try {
      return input.skip(n);
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  public int available() {
    try {
      return input.available();
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

  public void mark(int readlimit) {
    input.mark(readlimit);
  }

  public void reset() {
    try {
      input.reset();
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  public boolean markSupported() {
    return input.markSupported();
  }

  public InputStream raw() {
    return input;
  }

  public Input buffered() {
    return input(new BufferedInputStream(input));
  }

  public byte[] readAllBytes() {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    pumpTo(output(buffer));
    return buffer.toByteArray();
  }

  public Input pumpTo(Output output) {
    try {
      int oneByte;
      while ((oneByte = input.read()) != -1) {
        output.write(oneByte);
      }
    } catch (IOException e) {
      throw unchecked(e);
    }
    return this;
  }

  public Input pumpToAndFlush(Output output) {
    pumpTo(output);
    output.flush();
    return this;
  }

  public int peek() {
    mark(1);
    int oneByte = read();
    reset();
    return oneByte;
  }

  public Scanner scan(Charset charset) {
    return new Scanner(input, charset.name());
  }
}
