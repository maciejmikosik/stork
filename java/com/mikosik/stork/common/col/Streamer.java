package com.mikosik.stork.common.col;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.mikosik.stork.common.TypeToken;
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

  @SuppressWarnings("unchecked")
  public <R> Streamer<R> filter(Class<R> type) {
    return this
        .filter(type::isInstance)
        .map(element -> (R) element);
  }

  @SuppressWarnings("unchecked")
  public <R> Streamer<R> filter(TypeToken<R> type) {
    return this
        .filter(element -> type.getRawClass().isInstance(element))
        .map(element -> (R) element);
  }

  public <T> T apply(Fab<? super Streamer<E>, ? extends T> function) {
    return function.apply(this);
  }

  public void consume(Consumer<? super Streamer<E>> consumer) {
    consumer.accept(this);
  }

  public List<E> toList() {
    return stream.toList();
  }

  public <T> T toListAndApply(Fab<? super List<E>, ? extends T> function) {
    return function.apply(this.toList());
  }

  public void toListAndConsume(Consumer<? super List<E>> consumer) {
    consumer.accept(this.toList());
  }

  public Stream<E> toStream() {
    return stream;
  }

  public Iterator<E> toIterator() {
    return stream.iterator();
  }
}
