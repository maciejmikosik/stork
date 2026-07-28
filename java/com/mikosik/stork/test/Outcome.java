package com.mikosik.stork.test;

import static com.mikosik.stork.common.Throwables.runtimeException;
import static com.mikosik.stork.common.text.Outline.outline;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.mikosik.stork.common.Model;
import com.mikosik.stork.common.text.Outline;
import com.mikosik.stork.problem.Describer;
import com.mikosik.stork.problem.compile.CannotCompile;
import com.mikosik.stork.problem.compute.CannotCompute;

public class Outcome extends Model {
  private final Object object;

  private Outcome(Object object) {
    this.object = object;
  }

  public static Outcome outcome(CannotCompile cannotCompile) {
    return new Outcome(cannotCompile);
  }

  public static Outcome outcome(CannotCompute cannotCompute) {
    return new Outcome(cannotCompute);
  }

  public static Outcome outcome(byte[] stdout) {
    return new Outcome(stdout);
  }

  public Outline describe() {
    return switch (object) {
      case byte[] stdout -> outline(format(stdout));
      case CannotCompile cannotCompile -> Describer.describe(cannotCompile);
      case CannotCompute cannotCompute -> Describer.describe(cannotCompute);
      default -> throw runtimeException("unknown object " + object);
    };
  }

  private static String format(byte[] bytes) {
    return "[" + new String(bytes, UTF_8) + "]";
  }
}
