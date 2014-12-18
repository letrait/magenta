package org.magenta.core;

/**
 * Given an iterable of choices, implementation of this class provide a strategy to pick an element from that iterable.
 *
 * @author ngagnon
 *
 * @param <D>
 */
public interface PickStrategy {

  <D> D pick(Iterable<D> choices);
}
