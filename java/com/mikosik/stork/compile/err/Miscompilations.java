package com.mikosik.stork.compile.err;

import static com.mikosik.stork.compile.err.Miscompilation.miscompilation;

import com.mikosik.stork.compile.err.Miscompilation.Builder;

public class Miscompilations {
  public static Builder malformedImportLine() {
    return miscompilation("malformed import");
  }

  public static Builder importCollision() {
    return miscompilation("import collision");
  }

  public static Builder unexpectedToken() {
    return miscompilation("unexpected token");
  }

  public static Builder illegalCharacterInCode() {
    return miscompilation("illegal character in code");
  }

  public static Builder illegalCharacterInStringLiteral() {
    return miscompilation("illegal character in string literal");
  }

  public static Builder malformedWord() {
    return miscompilation("malformed word");
  }

  public static Builder unboundVariable() {
    return miscompilation("unbound variable");
  }

  public static Builder undefinedFunction() {
    return miscompilation("undefined function");
  }

  public static Builder duplicatedFunction() {
    return miscompilation("duplicated function");
  }
}
