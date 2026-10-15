package com.mikosik.stork.compile.err;

import static com.mikosik.stork.common.col.ImmutableList.cast;
import static com.mikosik.stork.common.col.ImmutableList.single;
import static com.mikosik.stork.common.col.Streamer.streamer;

import java.util.ArrayList;
import java.util.List;

import com.mikosik.stork.common.col.Streamer;

public class CompilerException extends RuntimeException {
  public final List<Problem> problems;

  private CompilerException(List<Problem> problems) {
    this.problems = problems;
  }

  public static CompilerException exception(List<Problem> problems) {
    return new CompilerException(cast(problems));
  }

  public static CompilerException exception(Problem problem) {
    return new CompilerException(single(problem));
  }

  public static <E> Streamer<E> gatherCompilerProblems(Streamer<E> streamer) {
    var elements = new ArrayList<E>();
    var problems = new ArrayList<Problem>();

    var iterator = streamer.toIterator();
    while (true) {
      try {
        if (iterator.hasNext()) {
          elements.add(iterator.next());
        } else {
          break;
        }
      } catch (CompilerException exception) {
        problems.addAll(exception.problems);
      }
    }
    verifyNoProblems(problems);
    return streamer(elements);
  }

  public static void verifyNoProblems(List<Problem> problems) {
    if (!problems.isEmpty()) {
      throw exception(problems);
    }
  }
}
