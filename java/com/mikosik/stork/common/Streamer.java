package com.mikosik.stork.common;

import static com.mikosik.stork.common.ImmutableList.listFrom;
import static com.mikosik.stork.common.Peekerator.peekerator;
import static java.util.Spliterator.ORDERED;
import static java.util.Spliterators.spliteratorUnknownSize;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.mikosik.stork.common.func.Functions.Fab;

public class Streamer<E> {
  protected final Iterator<E> iterator;

  protected Streamer(Iterator<E> iterator) {
    this.iterator = iterator;
  }

  private static <E> Streamer<E> streamer(Iterator<E> iterator) {
    return new Streamer<E>(iterator);
  }

  public static <E> Streamer<E> streamer(Iterable<E> iterable) {
    return streamer(iterable.iterator());
  }

  public Stream<E> stream() {
    boolean parallel = false;
    return StreamSupport.stream(
        spliteratorUnknownSize(iterator, ORDERED),
        parallel);
  }

  public <R> Streamer<R> map(Fab<? super E, ? extends R> mapping) {
    return streamer(new Iterator<R>() {

      public boolean hasNext() {
        return iterator.hasNext();
      }

      public R next() {
        return mapping.apply(iterator.next());
      }
    });
  }

  public Streamer<E> filter(Predicate<E> predicate) {
    var peekerator = peekerator(iterator);
    return streamer(new Iterator<E>() {
      public boolean hasNext() {
        skipTo(predicate, peekerator);
        return peekerator.hasNext();
      }

      public E next() {
        skipTo(predicate, peekerator);
        return peekerator.next();
      }

      private void skipTo(Predicate<E> predicate, Peekerator<E> peekerator) {
        while (peekerator.hasNext() && !predicate.test(peekerator.peek())) {
          peekerator.next();
        }
      }
    });
  }

  public <T> T apply(Fab<? super Streamer<E>, ? extends T> function) {
    return function.apply(this);
  }

  public List<E> toList() {
    return listFrom(iterator);
  }
}
