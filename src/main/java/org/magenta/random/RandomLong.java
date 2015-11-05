package org.magenta.random;

import java.util.Random;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

/**
 * Helper class that generates random longs.
 * @author ngagnon
 *
 */
public class RandomLong {

  private final Random random;

  private final Range<Long> constraint;
  private final long resolution;

  private static final Range<Long> ALL = Range.all();
  private static final Range<Long> EVERY_POSITIVE_BUT_ZERO = Range.greaterThan(0L);
  private static final Range<Long> EVERY_POSITIVE_INCLUDING_ZERO = Range.atLeast(0L);
  private static final Range<Long> EVERY_NEGATIVE_BUT_ZERO = Range.lessThan(0L);
  private static final Range<Long> EVERY_NEGATIVE_INCLUDING_ZERO = Range.atMost(0L);

  /**
   * Default constructor which defines no constraint and use a resolution of 1.
   *
   * @param random the random to use
   */
  public RandomLong(Random random) {
    this(random, ALL, 1L);
  }

  /**
   * Constructor for custom <code>RandomLong</code> instance. This constructor allow you to specify default values for <code>constraint</code> and
   * <code>resolution</code>.
   *
   * @param random the random to use
   * @param constraint the range from which to generate a <code>long</code>.
   * @param resolution the resolution
   */
  public RandomLong(Random random, Range<Long> constraint, long resolution) {
    this.random = random;
    this.constraint = constraint;
    this.resolution = resolution;
  }

  /**
   * Generate a {@code long} within the intersection formed by the specified {@code range} and this instance own range.
   *
   * @param range the specfied range
   * @return a generated {@code long}
   */
  public long any(final Range<Long> range) {

    Range<Long> r = range.intersection(constraint);

    if (r.isEmpty()) {
      throw new IllegalStateException(String.format(
          "The intersection of the passed in range %s and this class constrained range %s result in a empty range", range, constraint));
    }

    long upperBound = r.hasUpperBound() ? r.upperEndpoint() : Long.MAX_VALUE/2;
    long lowerBound = r.hasLowerBound() ? r.lowerEndpoint() : Long.MIN_VALUE/2;

    if (r.hasUpperBound() && BoundType.CLOSED == r.upperBoundType()) {
      upperBound++;
    }

    if (r.hasLowerBound() && BoundType.OPEN == r.lowerBoundType()) {
      lowerBound++;
    }

    long delta = Math.abs(upperBound - lowerBound);

    long randomLong = ((( Math.abs(this.random.nextLong()) % (delta / resolution))) * resolution) + lowerBound ;

    return randomLong;
  }

  /**
   * Generate a {@code long} within this instance own range.
   *
   * @return a generated {@code long}
   */
  public long any() {
    return any(ALL);
  }

  /**
   * Generate a positive {@code long} within this instance own range.
   *
   * @return a generated {@code long}
   */
  public long anyPositive() {
    return any(EVERY_POSITIVE_INCLUDING_ZERO);
  }

  /**
   * Generate a positive {@code long} (excluding zero) within this instance own range.
   *
   * @return a generated {@code long}
   */
  public long anyPositiveButZero() {
    return any(EVERY_POSITIVE_BUT_ZERO);
  }

  /**
   * Generate a positive {@code long} within this instance own range and having a maximum value of {@code max}.
   *
   * @param max the maximum possible value
   * @return a generated {@code long}
   */
  public long anyPositive(long max) {
    return any(Range.closedOpen(0L, max));
  }

  /**
   * Generate a positive {@code long} (excluding zero) within this instance own range  and having a maximum value of {@code max}.
   *
   * @param max the maximum possible value
   * @return a generated {@code long}
   */
  public long anyPositiveButZero(long max) {
    return any(Range.open(0L, max));
  }

  /**
   * Generate a negative {@code long} within this instance own range.
   *
   * @return a generated {@code long}
   */
  public long anyNegative() {
    return any(EVERY_NEGATIVE_INCLUDING_ZERO);
  }

  /**
   * Generate a negative {@code long} (excluding zero) within this instance own range.
   *
   * @return a generated {@code long}
   */
  public long anyNegativeButZero() {
    return any(EVERY_NEGATIVE_BUT_ZERO);
  }

  /**
   * Generate a negative {@code long} within this instance own range and having a minimum value of {@code max}.
   *
   * @param min the minimum possible value
   * @return a generated {@code long}
   */
  public long anyNegative(long min) {
    return any(Range.openClosed(min, 0L));
  }


  /**
   * Generate a negative {@code long} (excluding zero) within this instance own range  and having a minimum value of {@code max}.
   *
   * @param min the minimum possible value
   * @return a generated {@code long}
   */
  public long anyNegativeButZero(long min) {
    return any(Range.open(min, 0L));
  }

  /**
   * Return a new copy of this instance having the specified {@code resolution}.
   *
   * @param resolution the resolution
   * @return a new {@code RandomLong}
   */
  public RandomLong resolution(long resolution) {
    return new RandomLong(random, constraint, resolution);
  }

  /**
   * Return a new copy of this instance having the specified {@code constraint}.
   *
   * @param constraint the constraint
   * @return a new {@code RandomLong}
   */
  public RandomLong constraint(Range<Long> constraint) {
    return new RandomLong(random, this.constraint.intersection(constraint), resolution);
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
  public RandomLong constraint(long minIncluded, long maxIncluded) {
    return constraint(Range.closed(minIncluded, maxIncluded));
  }
}
