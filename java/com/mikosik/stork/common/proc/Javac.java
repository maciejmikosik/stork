package com.mikosik.stork.common.proc;

import static com.mikosik.stork.common.UncheckedInterruptedException.unchecked;
import static com.mikosik.stork.common.io.InputOutput.unchecked;

import java.io.IOException;

import com.mikosik.stork.common.io.Directory;
import com.mikosik.stork.common.io.File;

public class Javac {
  private Directory sourcepath;
  private Directory destination;
  private File sourcefile;

  private Javac() {}

  public static Javac javac() {
    return new Javac();
  }

  public Javac sourcepath(Directory sourcepath) {
    this.sourcepath = sourcepath;
    return this;
  }

  public Javac destination(Directory destination) {
    this.destination = destination;
    return this;
  }

  public Javac sourcefile(File sourcefile) {
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
