package com.mikosik.stork.common.proc;

import static java.util.Collections.unmodifiableList;

import java.util.LinkedList;
import java.util.List;

public class CommandBuilder {
  private final List<String> command = new LinkedList<>();

  private CommandBuilder(String program) {
    command.add(program);
  }

  public static CommandBuilder command(String program) {
    return new CommandBuilder(program);
  }

  public CommandBuilder arg(String arg) {
    command.add(arg);
    return this;
  }

  public CommandBuilder option(String key, String value) {
    return arg(key).arg(value);
  }

  public CommandBuilder optionIf(boolean condition, String key, String value) {
    return condition
        ? option(key, value)
        : this;
  }

  public List<String> build() {
    return unmodifiableList(command);
  }
}
