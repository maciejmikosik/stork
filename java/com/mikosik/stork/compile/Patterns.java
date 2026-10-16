package com.mikosik.stork.compile;

import static com.mikosik.stork.common.text.Regex.DIGIT;
import static com.mikosik.stork.common.text.Regex.LETTER;
import static com.mikosik.stork.common.text.Regex.SIGN;
import static com.mikosik.stork.common.text.Regex.oneOrMore;
import static com.mikosik.stork.common.text.Regex.or;
import static com.mikosik.stork.common.text.Regex.separatedBy;
import static com.mikosik.stork.common.text.Regex.zeroOrMore;
import static com.mikosik.stork.common.text.Regex.zeroOrOne;

public interface Patterns {
  String INTEGER_LITERAL = zeroOrOne(SIGN) + oneOrMore(DIGIT);
  String IDENTIFIER = LETTER + zeroOrMore(or(LETTER, DIGIT));
  String REFERENCE = separatedBy("/", IDENTIFIER);
  String IMPORT_LINE = REFERENCE + zeroOrOne(" " + IDENTIFIER);
}
