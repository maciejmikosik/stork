package com.mikosik.stork.common.proc;

import static com.mikosik.stork.common.SequenceBuilder.sequenceBuilderOf;
import static com.mikosik.stork.common.Throwables.runtimeException;
import static com.mikosik.stork.common.UncheckedInterruptedException.unchecked;
import static com.mikosik.stork.common.io.InputOutput.unchecked;

import java.io.IOException;

import com.mikosik.stork.common.io.Directory;
import com.mikosik.stork.common.io.File;

public class Javac {
  private Directory source;
  private Directory destination;
  private File seed;
  private int release;

  private Javac() {}

  public static Javac javac() {
    return new Javac();
  }

  public Javac source(Directory source) {
    this.source = source;
    return this;
  }

  public Javac destination(Directory destination) {
    this.destination = destination;
    return this;
  }

  public Javac seed(File seed) {
    this.seed = seed;
    return this;
  }

  public Javac release(int release) {
    this.release = release;
    return this;
  }

  public void execute() {
    try {
      var command = sequenceBuilderOf("javac")
          .addAll("-sourcepath", source.toString())
          .addAll("-d", destination.toString())
          .andAllIf(release != 0, "--release", Integer.toString(release))
          .addAll(seed.toString())
          .build();
      int exitValue = new ProcessBuilder()
          .command(command)
          .inheritIO()
          .start()
          .waitFor();
      check(exitValue);
    } catch (IOException e) {
      throw unchecked(e);
    } catch (InterruptedException e) {
      throw unchecked(e);
    }
  }

  public static void check(int exitValue) {
    if (exitValue != 0) {
      throw runtimeException("code: %d", exitValue);
    }
  }
}
