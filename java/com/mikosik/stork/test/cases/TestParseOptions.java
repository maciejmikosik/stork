package com.mikosik.stork.test.cases;

import static com.mikosik.stork.ParseOptions.parseOptions;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.quackery.report.AssertException.assertEquals;
import static org.quackery.run.Runners.expect;

import java.util.List;
import java.util.Map;

import org.quackery.Test;

public class TestParseOptions {
  public static Test testParseOptions() {
    return suite("option parser")
        .add(suite("parses")
            .add(suite("no options")
                .add(parses(asList(), Map.of())))
            .add(suite("single option")
                .add(parses(
                    asList("--option"),
                    Map.of("--option", asList())))
                .add(parses(
                    asList("--option", "argument"),
                    Map.of("--option", asList("argument"))))
                .add(parses(
                    asList("--option", "argumentA", "argumentB"),
                    Map.of("--option", asList("argumentA", "argumentB")))))
            .add(suite("two options")
                .add(parses(
                    asList("--optionA", "--optionB"),
                    Map.of(
                        "--optionA", asList(),
                        "--optionB", asList())))
                .add(parses(
                    asList("--optionA", "--optionB", "argument"),
                    Map.of(
                        "--optionA", asList(),
                        "--optionB", asList("argument"))))
                .add(parses(
                    asList("--optionA", "--optionB", "argumentA", "argumentB"),
                    Map.of(
                        "--optionA", asList(),
                        "--optionB", asList("argumentA", "argumentB"))))))
        .add(suite("not parses")
            .add(fails(asList("option"))));
  }

  private static Test parses(List<String> strings, Map<String, List<String>> expected) {
    return newCase(join(strings), () -> {
      assertEquals(parseOptions(strings), expected);
    });
  }

  private static Test fails(List<String> strings) {
    return expect(RuntimeException.class,
        newCase(join(strings), () -> {
          parseOptions(strings);
        }));
  }

  private static String join(List<String> strings) {
    return strings.stream().collect(joining(" "));
  }
}
