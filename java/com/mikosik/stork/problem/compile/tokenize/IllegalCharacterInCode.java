package com.mikosik.stork.problem.compile.tokenize;

import static com.mikosik.stork.compile.Problem.problem;

import com.mikosik.stork.compile.Problem;

public class IllegalCharacterInCode {
  public static Problem illegalCharacterInCode(byte character) {
    return problem("illegal character in code")
        .character(character)
        .build();
  }
}
