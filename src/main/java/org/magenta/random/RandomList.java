package org.magenta.random;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.magenta.DataSupplier;
import org.magenta.core.data.supplier.StaticDataSupplier;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;

/**
 * Helper class for random selection of objects from a list.
 *
 * @author ngagnon
 *
 * @param <E> the type of data produced by this class
 */
public class RandomList<E> {

  private final DataSupplier<E> values;
  private final RandomInteger integers;
  private final Random random;

  /**
   * Default constructor.
   *
   * @param random the random to use (for shuffling)
   * @param integers the {@code RandomInteger} to use (for picks)
   * @param values the list from which to pick values
   */
  public RandomList(Random random, RandomInteger integers, DataSupplier<E> values) {
    this.integers = integers;
    this.values = values;
    this.random = random;
  }

  /**
   * Pick any value within this instance own values.
   *
   * @return a randomly picked value
   */
  public E any() {
    Preconditions.checkState(!values.isEmpty(), "No items in the list to select, the list is empty");
    return values.get(integers.anyPositive(values.getSize()));
  }

  /**
   * Pick any value within this instance own values hile avoiding the one defined within {@code thoseOnes}.
   * @param thoseOnes the values to avoid
   * @return a randomly picked value
   */
  @SafeVarargs
  public final E anyBut(E... thoseOnes) {

    List<E> l = Lists.newArrayList(values);
    l.removeAll(Arrays.asList(thoseOnes));
    return l.get(integers.anyPositive(l.size()));
  }

  /**
   * Pick some values within this instance own values.
   *
   * @return a of random size list of values randomly picked from this instance own values.
   */
  public List<E> some() {
    Preconditions.checkState(!values.isEmpty(), "No items in the list to select, the list is empty");
    int numberOfItemsToPick = integers.anyPositive(values.getSize());

    return shuffle().list().subList(0, numberOfItemsToPick);
  }

  /**
   * Pick some values within this instance own values while avoiding the one defined within {@code thoseOnes}.
   *
   * @param thoseOnes the values to avoid
   * @return a random size list of values randomly picked from this instance own values.
   */
  @SafeVarargs
  public final List<E> someBut(E... thoseOnes) {
    Preconditions.checkState(!values.isEmpty(), "No items in the list to select, the list is empty");
    int numberOfItemsToPick = integers.anyPositive(values.getSize());
    List<E> l = Lists.newArrayList(values);
    l.removeAll(Arrays.asList(thoseOnes));

    Collections.shuffle(l, random);

    return l.subList(0, numberOfItemsToPick);
  }

  /**
   * Pick some values within this instance own values.
   * @param numberOfItemsToPick the number of items to pick
   * @return a list of values randomly picked from this instance own values.
   */
  public List<E> some(int numberOfItemsToPick) {
    Preconditions.checkState(!values.isEmpty(), "No items in the list to select, the list is empty");
    Preconditions.checkState(values.getSize() >= numberOfItemsToPick, "The number Of items to pick is greater than the number of items in the list");

    return shuffle().list().subList(0, numberOfItemsToPick);
  }

  /**
   * Shuffle the values of this instance.
   *
   * @return this instance
   */
  public RandomList<E> shuffle() {

    List<E> l = Lists.newArrayList(values);

    Collections.shuffle(l, random);

    return new RandomList<>(random, integers, new StaticDataSupplier(l, TypeToken.of(values.get(0).getClass())));
  }

  /**
   * Return the values contained in this {@code RandomList}.
   *
   * @return the values
   */
  public List<E> list() {
    return ImmutableList.copyOf(this.values);
  }
}
