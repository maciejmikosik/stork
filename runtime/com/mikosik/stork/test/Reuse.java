package com.mikosik.stork.test;

import static com.mikosik.stork.common.Sequence.sequence;
import static com.mikosik.stork.compile.Bind.join;
import static com.mikosik.stork.compile.Stars.build;
import static com.mikosik.stork.compile.Stars.langModule;
import static com.mikosik.stork.compile.Stars.verify;
import static com.mikosik.stork.program.ProgramModule.programModule;

import com.mikosik.stork.model.Module;

public class Reuse {
  private static final Module BUILT_LANG_MODULE = build(langModule());
  private static final Module BUILT_PROGRAM_MODULE = build(programModule());

  public static final Module LANG_MODULE = verify(BUILT_LANG_MODULE);

  public static final Module LANG_AND_PROGRAM_MODULE = verify(join(sequence(
      BUILT_LANG_MODULE,
      BUILT_PROGRAM_MODULE)));
}
