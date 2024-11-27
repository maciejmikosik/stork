package com.mikosik.stork.common.proc;

import static com.mikosik.stork.common.UncheckedInterruptedException.unchecked;
import static com.mikosik.stork.common.io.InputOutput.unchecked;

import java.io.IOException;
import java.nio.file.Path;

public class Javac {
  private Path sourcepath;
  private Path destination;
  private Path sourcefile;

  private Javac() {}

  public static Javac javac() {
    return new Javac();
  }

  public Javac sourcepath(Path sourcepath) {
    this.sourcepath = sourcepath;
    return this;
  }

  public Javac destination(Path destination) {
    this.destination = destination;
    return this;
  }

  public Javac sourcefile(Path sourcefile) {
    this.sourcefile = sourcefile;
    return this;
  }

  public void execute() {
    try {
      int exitValue = new ProcessBuilder()
          .command("javac",
              "-sourcepath", sourcepath.toString(),
              "-d", destination.toString(),
              sourcefile.toString())
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
      throw new RuntimeException("code: " + exitValue);
    }
  }
}
