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
  private Directory sourceDirectory;
  private File destinationFile;

  private Zip() {}

  public static Zip zip() {
    return new Zip();
  }

  public Zip sourceDirectory(Directory sourceDirectory) {
    this.sourceDirectory = sourceDirectory;
    return this;
  }

  public Zip destinationFile(File destinationFile) {
    this.destinationFile = destinationFile;
    return this;
  }

  public void execute() {
    try {
      int exitValue = new ProcessBuilder()
          .command(shellExpansion(
              "zip",
              "--quiet", "-X", "--recurse-paths",
              destinationFile.toString(), "./*"))
          .directory(sourceDirectory.path.toFile())
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
