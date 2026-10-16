package com.mikosik.stork.compile;

import static com.mikosik.stork.common.text.Regex.oneOrMore;
import static com.mikosik.stork.common.text.Regex.LETTER;
import static com.mikosik.stork.common.text.Regex.zeroOrOne;
import static com.mikosik.stork.common.text.Regex.separatedBy;

public interface Patterns {
  String VARIABLE = oneOrMore(LETTER);
  String REFERENCE = separatedBy("/", VARIABLE);
  String IMPORT_LINE = REFERENCE + zeroOrOne(" " + VARIABLE);
}
