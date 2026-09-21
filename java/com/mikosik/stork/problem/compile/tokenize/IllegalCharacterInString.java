package com.mikosik.stork.problem.compile.tokenize;

import static com.mikosik.stork.compile.Problem.problem;

import com.mikosik.stork.compile.Problem;

public class IllegalCharacterInString {
  public static Problem illegalCharacterInString(byte character) {
    return problem("illegal character in string")
        .character(character)
        .build();
  }
}
