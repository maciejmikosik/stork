package com.mikosik.stork.problem.compile;

import static com.mikosik.stork.common.ImmutableList.cast;
import static com.mikosik.stork.common.ImmutableList.single;
import static com.mikosik.stork.common.Streamer.streamer;

import java.util.ArrayList;
import java.util.List;

import com.mikosik.stork.common.Streamer;

public class CompilerException extends RuntimeException {
  public final List<CannotCompile> problems;

  private CompilerException(List<CannotCompile> problems) {
    this.problems = problems;
  }

  public static CompilerException exception(
      List<? extends CannotCompile> problems) {
    return new CompilerException(cast(problems));
  }

  public static CompilerException exception(CannotCompile problem) {
    return new CompilerException(single(problem));
  }

  public static <E> Streamer<E> gatherCompilerProblems(Streamer<E> streamer) {
    var elements = new ArrayList<E>();
    var problems = new ArrayList<CannotCompile>();

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
    if (problems.isEmpty()) {
      return streamer(elements);
    } else {
      throw exception(problems);
    }
  }
}
