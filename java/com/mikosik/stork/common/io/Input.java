package com.mikosik.stork.common.io;

import static com.mikosik.stork.common.io.Buffer.newBuffer;
import static com.mikosik.stork.common.io.InputOutput.unchecked;
import static com.mikosik.stork.common.io.MaybeByte.maybeByte;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.NoSuchElementException;
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

  public static Input input(Path file) {
    try {
      return input(Files.newInputStream(file));
    } catch (IOException e) {
      throw unchecked(e);
    }
  }

  public static Input tryInput(Path file) {
    return Files.isRegularFile(file)
        ? input(file)
        : input(new byte[0]);
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

  public byte[] readAllBytes() {
    Buffer buffer = newBuffer();
    pumpTo(buffer.asOutput());
    return buffer.bytes();
  }

  public byte[] readAllBytes(IntPredicate predicate) {
    Buffer buffer = newBuffer();
    Output output = buffer.asOutput();
    MaybeByte maybeByte;
    while ((maybeByte = peek()).hasByte() && predicate.test(maybeByte.getByte())) {
      output.write(read().getByte());
    }
    return buffer.bytes();
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

  public Iterator<Byte> iterator() {
    return new Iterator<>() {
      public boolean hasNext() {
        try {
          return input.available() > 0;
        } catch (IOException e) {
          throw unchecked(e);
        }
      }

      public Byte next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        try {
          return (byte) input.read();
        } catch (IOException e) {
          throw unchecked(e);
        }
      }
    };
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

  public BufferedReader bufferedReader(Charset charset) {
    return new BufferedReader(new InputStreamReader(input, charset));
  }

  public Scanner scan(Charset charset) {
    return new Scanner(input, charset.name());
  }
}
