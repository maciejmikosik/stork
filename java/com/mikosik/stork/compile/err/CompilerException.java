package com.mikosik.stork.compile.err;

import static com.mikosik.stork.common.col.ImmutableList.cast;
import static com.mikosik.stork.common.col.ImmutableList.single;
import static com.mikosik.stork.common.col.Streamer.streamer;
import static com.mikosik.stork.compile.err.Describer.describe;

import java.util.ArrayList;
import java.util.List;

import com.mikosik.stork.common.col.Streamer;

public class CompilerException extends RuntimeException {
  public final List<Miscompilation> miscompilations;

  private CompilerException(List<Miscompilation> miscompilations) {
    this.miscompilations = miscompilations;
  }

  public static CompilerException exception(List<Miscompilation> miscompilations) {
    return new CompilerException(cast(miscompilations));
  }

  public static CompilerException exception(Miscompilation miscompilations) {
    return new CompilerException(single(miscompilations));
  }

  public String getMessage() {
    return describe(this).toString();
  }

  public static <E> Streamer<E> gatherMiscompilations(Streamer<E> streamer) {
    var elements = new ArrayList<E>();
    var miscompilations = new ArrayList<Miscompilation>();

    var iterator = streamer.toIterator();
    while (true) {
      try {
        if (iterator.hasNext()) {
          elements.add(iterator.next());
        } else {
          break;
        }
      } catch (CompilerException exception) {
        miscompilations.addAll(exception.miscompilations);
      }
    }
    verifyNo(miscompilations);
    return streamer(elements);
  }

  public static void verifyNo(List<Miscompilation> miscompilations) {
    if (!miscompilations.isEmpty()) {
      throw exception(miscompilations);
    }
  }
}
