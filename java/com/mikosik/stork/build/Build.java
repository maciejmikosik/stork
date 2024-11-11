package com.mikosik.stork.build;

import static com.mikosik.stork.common.StandardOutput.out;
import static com.mikosik.stork.common.io.InputOutput.path;

public class Build {
  public static void main(String... args) {
    out("building stork shebang binary");
    out("working directory: %s", path(".").toAbsolutePath());
  }
}
