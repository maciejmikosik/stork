package com.mikosik.stork;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ParseOptions {
  public static Map<String, List<String>> parseOptions(List<String> tokens) {
    return parseOptionsImpl(new LinkedList<>(tokens));
  }

  private static Map<String, List<String>> parseOptionsImpl(List<String> token) {
    var result = new HashMap<String, List<String>>();
    while (!token.isEmpty()) {
      var option = token.remove(0);
      if (!isOption(option)) {
        throw new RuntimeException("expected option, not " + option);
      }
      var arguments = new LinkedList<String>();
      while (!token.isEmpty() && !isOption(token.get(0))) {
        arguments.add(token.remove(0));
      }
      result.put(option, arguments);
    }
    return result;
  }

  private static boolean isOption(String token) {
    return token.startsWith("--");
  }
}
