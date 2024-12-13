package com.mikosik.stork.test.cases;

import static com.mikosik.stork.common.Sequence.sequenceOf;
import static com.mikosik.stork.test.QuackeryHelper.assertSame;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.quackery.run.Runners.expect;

import java.util.List;

import org.quackery.Test;
import org.quackery.contract.collection.CollectionContract;

import com.mikosik.stork.common.Sequence;

public class TestSequence {
  private static final Object a = "a", b = "b", c = "c", x = "x";

  public static Test testSequence() {
    return suite("sequence")
        .add(newCase("factory copies varargs", () -> {
          var elements = new Object[] { a, b, c };
          var sequence = sequenceOf(elements);
          elements[1] = x;
          assertSame(sequence.get(1), b);
        }))
        .add(expect(UnsupportedOperationException.class, newCase("cannot set element", () -> {
          var sequence = sequenceOf(a, b, c);
          sequence.set(1, x);
        })))
        .add(new CollectionContract()
            .implementing(List.class)
            .withFactory("asSequence")
            .immutable()
            .test(Sequence.class));
  }
}
