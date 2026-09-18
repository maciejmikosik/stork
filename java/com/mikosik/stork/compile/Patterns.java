package com.mikosik.stork.compile;

import static com.mikosik.stork.common.Regex.atLeastOne;
import static com.mikosik.stork.common.Regex.letter;
import static com.mikosik.stork.common.Regex.maybe;
import static com.mikosik.stork.common.Regex.separated;

public interface Patterns {
  String VARIABLE = atLeastOne(letter);
  String IDENTIFIER = separated("/", VARIABLE);
  String IMPORT_LINE = IDENTIFIER + maybe(" " + VARIABLE);
}
