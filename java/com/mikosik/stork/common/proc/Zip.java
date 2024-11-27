package com.mikosik.stork.common.proc;

import static com.mikosik.stork.common.UncheckedInterruptedException.unchecked;
import static com.mikosik.stork.common.io.InputOutput.unchecked;
import static java.lang.String.join;
import static java.util.Arrays.asList;

import java.io.IOException;
import java.util.List;

import com.mikosik.stork.common.io.Directory;
import com.mikosik.stork.common.io.File;

public class Zip {
  private Directory source;
  private File destination;

  private Zip() {}

  public static Zip zip() {
    return new Zip();
  }

  public Zip source(Directory source) {
    this.source = source;
    return this;
  }

  public Zip destination(File destination) {
    this.destination = destination;
    return this;
  }

  public void execute() {
    try {
      int exitValue = new ProcessBuilder()
          .command(shellExpansion(
              "zip",
              "--quiet", "-X", "--recurse-paths",
              destination.toString(), "./*"))
          .directory(source.path.toFile())
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

  private static List<String> shellExpansion(String... command) {
    return asList("sh", "-c", join(" ", command));
  }

  public static void check(int exitValue) {
    if (exitValue != 0) {
      throw new RuntimeException("code: " + exitValue);
    }
  }
}
