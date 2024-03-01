package com.mikosik.stork.common;

import java.util.Iterator;

public class PeekableIterator<E> implements Iterator<E> {
  private final Iterator<E> iterator;

  private boolean hasPeeked;
  private E peeked;

  private PeekableIterator(Iterator<E> iterator) {
    this.iterator = iterator;
  }

  public static <E> PeekableIterator<E> peekable(Iterator<E> iterator) {
    return new PeekableIterator<E>(iterator);
  }

  public boolean hasNext() {
    return hasPeeked || iterator.hasNext();
  }

  public E next() {
    if (hasPeeked) {
      hasPeeked = false;
      return peeked;
    } else {
      return iterator.next();
    }
  }

  public E peek() {
    if (hasPeeked) {
      return peeked;
    } else {
      peeked = iterator.next();
      hasPeeked = true;
      return peeked;
    }
  }
}
