package com.mikosik.stork.common;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.mikosik.stork.common.func.Functions.Fab;

public class Streamer<E> {
  private final Stream<E> stream;

  private Streamer(Stream<E> stream) {
    this.stream = stream;
  }

  private static <E> Streamer<E> streamer(Stream<E> stream) {
    return new Streamer<E>(stream);
  }

  public static <E> Streamer<E> streamer(Iterable<E> iterable) {
    return streamer(StreamSupport.stream(iterable.spliterator(), false));
  }

  public <R> Streamer<R> map(Fab<? super E, ? extends R> mapping) {
    return streamer(stream.map(mapping));
  }

  public Streamer<E> filter(Predicate<E> predicate) {
    return streamer(stream.filter(predicate));
  }

  public static <E> Streamer<E> flatten(Streamer<? extends Streamer<E>> streamers) {
    return streamer(streamers.toStream()
        .flatMap(Streamer::toStream));
  }

  public <T> T apply(Fab<? super Streamer<E>, ? extends T> function) {
    return function.apply(this);
  }

  public List<E> toList() {
    return stream.toList();
  }

  public <T> T toListAndApply(Fab<? super List<E>, ? extends T> function) {
    return function.apply(this.toList());
  }

  public Stream<E> toStream() {
    return stream;
  }

  public Iterator<E> toIterator() {
    return stream.iterator();
  }
}
