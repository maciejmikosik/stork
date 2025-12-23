package com.mikosik.stork.program;

public class Task {
  public final Program program;
  public final Terminal terminal;

  private Task(Program program, Terminal terminal) {
    this.program = program;
    this.terminal = terminal;
  }

  public static Task task(Program program, Terminal terminal) {
    return new Task(program, terminal);
  }
}
