package org.magenta.random;

import java.util.Random;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

/**
 * Helper class that generates random {@code  shorts}.
 * @author ngagnon
 *
 */
public class RandomShort {

  private final Random random;

  private final Range<Short> constraint;
  private final short resolution;

  private static final Range<Short> ALL = Range.all();
  private static final Range<Short> EVERY_POSITIVE_BUT_ZERO = Range.greaterThan((short) 0);
  private static final Range<Short> EVERY_POSITIVE_INCLUDING_ZERO = Range.atLeast((short) 0);
  private static final Range<Short> EVERY_NEGATIVE_BUT_ZERO = Range.lessThan((short) 0);
  private static final Range<Short> EVERY_NEGATIVE_INCLUDING_ZERO = Range.atMost((short) 0);

  /**
   * Construct a {@link RandomShort} instance using a resolution of 1 and no
   * constraint.
   *
   * @param random
   *          used to generate random numbers
   */
  public RandomShort(Random random) {
    this(random, ALL, (short) 1);
  }

  /**
   * Construct a {@link RandomShort} instance using the specified
   * <code>resolution</code> within the specified <code>constraint</code>.
   *
   * @param random
   *          used to generate random numbers
   * @param constraint
   *          the integer range from which to select an integer
   * @param resolution
   *          the minimum possible delta between two generated integers
   */
  public RandomShort(Random random, Range<Short> constraint, short resolution) {
    this.random = random;
    this.constraint = constraint;
    this.resolution = resolution;
  }

  /**
   * Generate a random short within the intersection of the specified
   * <code>range</code> and this instance current range.
   *
   * @param range
   *          the desired range
   * @return a randomly generated integer
   */
  public short any(final Range<Short> range) {

    Range<Short> r = range.intersection(constraint);

    if (r.isEmpty()) {
      throw new IllegalStateException(String.format(
          "The intersection of the passed in range %s and this class constrained range %s result in a empty range", range, constraint));
    }

    short upperBound = r.hasUpperBound() ? r.upperEndpoint() : Short.MAX_VALUE;
    short lowerBound = r.hasLowerBound() ? r.lowerEndpoint() : Short.MIN_VALUE;

    if (BoundType.CLOSED == r.upperBoundType()) {
      // upperBound is not included in the random.nextInt() method
      upperBound++;
    }

    if (BoundType.OPEN == r.lowerBoundType()) {
      // lowerBound is included in the random.nextInt() method
      lowerBound++;
    }

    short delta = (short) Math.abs(upperBound - lowerBound);

    short anyIntRange = (short) ((this.random.nextInt(delta / resolution) * resolution) + lowerBound);

    return anyIntRange;
  }

  /**
   * Generate a random {@code short} within this instance current range.
   *
   * @return a randomly generated  {@code short}
   */
  public short any() {
    return any(ALL);
  }

  /**
   * Generate a random  {@code short} within the intersection of the range specified by
   * <code>includedMin</code> and <code>includedMax</code> and this instance
   * current range.
   *
   * @param includedMin
   *          the lower bound
   * @param includedMax
   *          the upper bound
   * @return a randomly generated  {@code short}
   */
  public short anyBetween(short includedMin, short includedMax) {
    return any(Range.closed(includedMin, includedMax));
  }


  /**
   * Generate a random positive  {@code short} (including zero) that is within this
   * instance current range.
   *
   * @return a randomly generated  {@code short}
   */
  public short anyPositive() {
    return any(EVERY_POSITIVE_INCLUDING_ZERO);
  }


  /**
   * Generate a random positive  {@code short} (excluding zero) that is within this
   * instance current range.
   *
   * @return a randomly generated  {@code short}
   */
  public short anyPositiveButZero() {
    return any(EVERY_POSITIVE_BUT_ZERO);
  }


  /**
   * Generate a random positive  {@code short} (including zero) that is within this
   * instance current range and below the specified <code>max</code>.
   *
   * @param max
   *          the maximum value
   * @return a randomly generated  {@code short}
   */
  public short anyPositive(short max) {
    return any(Range.closedOpen((short) 0, max));
  }

  /**
   * Generate a random positive  {@code short} (excluding zero) that is within this
   * instance current range and below the specified <code>max</code>.
   *
   * @param max
   *          the maximum value
   * @return a randomly generated  {@code short}
   */
  public short anyPositiveButZero(short max) {
    return any(Range.open((short) 0, max));
  }



  /**
   * Generate a random negative  {@code short} (including zero) that is within this
   * instance current range.
   *
   * @return a randomly generated  {@code short}
   */
  public short anyNegative() {
    return any(EVERY_NEGATIVE_INCLUDING_ZERO);
  }

  /**
   * Generate a random negative  {@code short} (excluding zero) that is within this
   * instance current range.
   *
   * @return a randomly generated  {@code short}
   */
  public short anyNegativeButZero() {
    return any(EVERY_NEGATIVE_BUT_ZERO);
  }


  /**
   * Generate a random negative  {@code short} (including zero) that is within this
   * instance current range and above the specified <code>min</code>.
   *
   * @param min
   *          the maximum value
   * @return a randomly generated  {@code short}
   */
  public short anyNegative(short min) {
    return any(Range.openClosed(min, (short) 0));
  }


  /**
   * Generate a random negative  {@code short} (excluding zero) that is within this
   * instance current range and above the specified <code>min</code>.
   *
   * @param min
   *          the maximum value
   * @return a randomly generated  {@code short}
   */
  public short anyNegativeButZero(short min) {
    return any(Range.open(min, (short) 0));
  }

  /**
   * Return a new copy of this instance having the specified
   * <code>resolution</code>.
   *
   * @param resolution
   *          the resolution
   * @return a new instance
   */
  public RandomShort resolution(short resolution) {
    return new RandomShort(random, constraint, resolution);
  }


  /**
   * Return a new copy of this instance having the specified
   * <code>constraint</code>.
   *
   * @param constraint
   *          the constraint
   * @return a new instance
   */
  public RandomShort constraint(Range<Short> constraint) {
    return new RandomShort(random, this.constraint.intersection(constraint), resolution);
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
  public RandomShort constraint(short minIncluded, short maxIncluded) {
    return constraint(Range.closed(minIncluded, maxIncluded));
  }

}
