package org.magenta.random;

import java.util.List;
import java.util.Random;

import com.google.common.base.Preconditions;
import com.google.common.collect.BoundType;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;

/**
 * Helper class that generates random integers.
 * @author ngagnon
 *
 */
public class RandomInteger {

  private Random random;

  private Range<Integer> constraint;
  private int resolution;

  private static final Range<Integer> ALL = Range.all();
  private static final Range<Integer> EVERY_POSITIVE_BUT_ZERO = Range.greaterThan(0);
  private static final Range<Integer> EVERY_POSITIVE_INCLUDING_ZERO = Range.atLeast(0);
  private static final Range<Integer> EVERY_NEGATIVE_BUT_ZERO = Range.lessThan(0);
  private static final Range<Integer> EVERY_NEGATIVE_INCLUDING_ZERO = Range.atMost(0);

  /**
   * Construct a {@link RandomInteger} instance using a resolution of 1 and no
   * constraint.
   *
   * @param random
   *          used to generate random numbers
   */
  public RandomInteger(Random random) {
    this(random, ALL, 1);
  }

  /**
   * Construct a {@link RandomInteger} instance using the specified
   * <code>resolution</code> within the specified <code>constraint</code>.
   *
   * @param random
   *          used to generate random numbers
   * @param constraint
   *          the integer range from which to select an integer
   * @param resolution
   *          the minimum possible delta between two generated integers
   */
  public RandomInteger(Random random, Range<Integer> constraint, int resolution) {
    this.random = random;
    this.constraint = constraint;
    this.resolution = resolution;
  }

  /**
   * Generate a random integer within the intersection of the specified
   * <code>range</code> and this instance current range.
   *
   * @param range
   *          the desired range
   * @return a randomly generated integer
   */
  public int any(final Range<Integer> range) {

    Range<Integer> r = range.intersection(constraint);

    if (r.isEmpty()) {
      throw new IllegalStateException(String.format(
          "The intersection of the passed in range %s and this class constrained range %s result in a empty range", range, constraint));
    }

    int upperBound = r.hasUpperBound() ? r.upperEndpoint() : Integer.MAX_VALUE;
    int lowerBound = r.hasLowerBound() ? r.lowerEndpoint() : Integer.MIN_VALUE;

    if (r.hasUpperBound() && BoundType.CLOSED == r.upperBoundType()) {
      // upperBound is not included in the random.nextInt() method
      upperBound++;
    }

    if (r.hasLowerBound() && BoundType.OPEN == r.lowerBoundType()) {
      // lowerBound is included in the random.nextInt() method
      lowerBound++;
    }

    int randomInt;

    if (lowerBound < 0 && upperBound > 0) {
      // special case
      int lowerRandomInt = (((this.random.nextInt((-lowerBound) / resolution))) * resolution) + lowerBound;
      int upperRandomInt = (((this.random.nextInt((upperBound) / resolution))) * resolution);
      randomInt = lowerRandomInt + upperRandomInt;

    } else {
      int delta = Math.abs(upperBound - lowerBound);

      randomInt = (this.random.nextInt(delta / resolution) * resolution) + lowerBound;
    }

    return randomInt;
  }

  /**
   * Generate a random integer within this instance current range.
   *
   * @return a randomly generated integer
   */
  public int any() {
    return any(ALL);
  }

  public List<Integer> some(int size) {

    ContiguousSet<Integer> a = ContiguousSet.create(constraint, DiscreteDomain.integers());

    Preconditions.checkArgument(size <= a.size(), "the number of items to pick (%s) must be lower than the number of integers available in the range (%s): ",size, a.size());

    RandomList<Integer> integers = new RandomList<Integer>(random, this, Lists.newArrayList(a));

    return integers.some(size);

  }

  /**
   * Generate a random integer within the intersection of the range specified by
   * <code>includedMin</code> and <code>includedMax</code> and this instance
   * current range.
   *
   * @param includedMin
   *          the lower bound
   * @param includedMax
   *          the upper bound
   * @return a randomly generated integer
   */
  public int anyBetween(int includedMin, int includedMax) {
    return any(Range.closed(includedMin, includedMax));
  }

  /**
   * Generate a random positive integer (including zero) that is within this
   * instance current range.
   *
   * @return a randomly generated integer
   */
  public int anyPositive() {
    return any(EVERY_POSITIVE_INCLUDING_ZERO);
  }

  /**
   * Generate a random positive integer (excluding zero) that is within this
   * instance current range.
   *
   * @return a randomly generated integer
   */
  public int anyPositiveButZero() {
    return any(EVERY_POSITIVE_BUT_ZERO);
  }

  /**
   * Generate a random positive integer (including zero) that is within this
   * instance current range and below the specified <code>max</code>.
   *
   * @param max
   *          the maximum value
   * @return a randomly generated integer
   */
  public int anyPositive(int max) {
    return any(Range.closedOpen(0, max));
  }

  /**
   * Generate a random positive integer (excluding zero) that is within this
   * instance current range and below the specified <code>max</code>.
   *
   * @param max
   *          the maximum value
   * @return a randomly generated integer
   */
  public int anyPositiveButZero(int max) {
    return any(Range.open(0, max));
  }

  /**
   * Generate a random negative integer (including zero) that is within this
   * instance current range.
   *
   * @return a randomly generated integer
   */
  public int anyNegative() {
    return any(EVERY_NEGATIVE_INCLUDING_ZERO);
  }

  /**
   * Generate a random negative integer (excluding zero) that is within this
   * instance current range.
   *
   * @return a randomly generated integer
   */
  public int anyNegativeButZero() {
    return any(EVERY_NEGATIVE_BUT_ZERO);
  }

  /**
   * Generate a random negative integer (including zero) that is within this
   * instance current range and above the specified <code>min</code>.
   *
   * @param min
   *          the maximum value
   * @return a randomly generated integer
   */
  public int anyNegative(int min) {
    return any(Range.openClosed(min, 0));
  }

  /**
   * Generate a random negative integer (excluding zero) that is within this
   * instance current range and above the specified <code>min</code>.
   *
   * @param min
   *          the maximum value
   * @return a randomly generated integer
   */
  public int anyNegativeButZero(int min) {
    return any(Range.open(min, 0));
  }

  /**
   * Return a new copy of this instance having the specified
   * <code>resolution</code>.
   *
   * @param resolution
   *          the resolution
   * @return a new instance
   */
  public RandomInteger resolution(int resolution) {
    return new RandomInteger(random, constraint, resolution);
  }

  /**
   * Return a new copy of this instance having the specified
   * <code>constraint</code>.
   *
   * @param constraint
   *          the constraint
   * @return a new instance
   */
  public RandomInteger constraint(Range<Integer> constraint) {
    return new RandomInteger(random, this.constraint.intersection(constraint), resolution);
  }

  /**
   * Return a new copy of this instance having the range specified by the
   * interval <code>minIncluded</code> and <code>maxIncluded</code>.
   *
   * @param minIncluded
   *          lower bound
   * @param maxIncluded
   *          upper bound
   * @return a new instance
   */
  public RandomInteger constraint(int minIncluded, int maxIncluded) {
    return constraint(Range.closed(minIncluded, maxIncluded));
  }
}
