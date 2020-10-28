package com.mikosik.stork.tool.link;

import static com.mikosik.stork.common.InputOutput.readResource;
import static com.mikosik.stork.tool.compile.Modeler.modelModule;
import static com.mikosik.stork.tool.compile.Parser.parse;

import com.mikosik.stork.data.model.Module;

public class Repository {
  private Repository() {}

  public static Repository repository() {
    return new Repository();
  }

  public Module module(String fileName) {
    return modelModule(parse(code(fileName)));
  }

  private static String code(String fileName) {
    return readResource(Repository.class, fileName);
  }
}
