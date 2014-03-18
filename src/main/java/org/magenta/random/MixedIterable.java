package org.magenta.random;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * A composite {@link Iterable} that iterates through a list of iterables randomly giving the impression that the data is shuffled.
 *
 * @author ngagnon
 *
 * @param <D> the type of data being iterated
 */
public class MixedIterable<D> implements Iterable<D> {

  private final Iterable<? extends Iterable<? extends D>> iterables;
  private final Randoms randomizer;

  MixedIterable(Iterable<? extends Iterable<? extends D>> iterables, Randoms randomizer) {
    this.iterables = iterables;
    this.randomizer = randomizer;
  }

  @Override
  public Iterator<D> iterator() {
    return new RandomlyMixedIterator(iterables);
  }

  private class RandomlyMixedIterator implements Iterator<D> {

    private final List<Iterator<? extends D>> iterators;

    private RandomlyMixedIterator(Iterable<? extends Iterable<? extends D>> iterables) {
      this.iterators = Lists.newArrayList();

      for (Iterable<? extends D> it : iterables) {
        if (!Iterables.isEmpty(iterables)) {
          iterators.add(it.iterator());
        }
      }
    }

    @Override
    public boolean hasNext() {
      return !iterators.isEmpty();
    }

    @Override
    public D next() {

      Iterator<? extends D> iterator = randomizer.iterable(iterators).any();

      D e = iterator.next();

      if (!iterator.hasNext()) {
        iterators.remove(iterator);
      }

      return e;
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException("remove not supported on RandomlyMixedIterable");
    }

  }
}
