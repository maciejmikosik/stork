package com.mikosik.stork.compile;

import static com.mikosik.stork.common.text.Regex.atLeastOne;
import static com.mikosik.stork.common.text.Regex.letter;
import static com.mikosik.stork.common.text.Regex.maybe;
import static com.mikosik.stork.common.text.Regex.separated;

public interface Patterns {
  String VARIABLE = atLeastOne(letter);
  String IDENTIFIER = separated("/", VARIABLE);
  String IMPORT_LINE = IDENTIFIER + maybe(" " + VARIABLE);
}
