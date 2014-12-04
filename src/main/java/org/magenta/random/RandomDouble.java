package org.magenta.random;

import java.util.Random;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

/**
 * Helper class that generates randomly selected double.  The algorithm is weak when generating high values, as they tend to the
 * upper limit, that is why the default "all" range is limited between -1.0E7 and 1.0E7.
 *
 * @author ngagnon
 *
 */
public class RandomDouble {

  public static final int DEFAULT_NUMBER_OF_DECIMAL_PLACES = 10;
  private static final double SCALE = 10D;

  private Random random;
  private double scale;
  private double lowestIncrement;
  private int numberOfDecimalPlaces;
  private Range<Double> constraint;

  private static final Range<Double> ALL = Range.all();
  private static final Range<Double> EVERY_POSITIVE_BUT_ZERO = Range.greaterThan(0D);
  private static final Range<Double> EVERY_POSITIVE_INCLUDING_ZERO = Range.atLeast(0D);
  private static final Range<Double> EVERY_NEGATIVE_BUT_ZERO = Range.lessThan(0D);
  private static final Range<Double> EVERY_NEGATIVE_INCLUDING_ZERO = Range.atMost(0D);

  /**
   * Default constructor.
   *
   * @param random
   *          a random
   */
  public RandomDouble(Random random) {
    this(random, DEFAULT_NUMBER_OF_DECIMAL_PLACES, ALL);
  }

  /**
   * @param random
   *          a random
   * @param numberOfDecimalPlaces
   *          the desired number of decimal places in the generated doubles
   */
  public RandomDouble(Random random, int numberOfDecimalPlaces) {
    this(random, numberOfDecimalPlaces, ALL);
  }

  /**
   * @param random
   *          a random
   * @param numberOfDecimalPlaces
   *          the desired number of decimal places in the generated doubles
   * @param constraint
   *          a range of double from which to randomly select
   */
  public RandomDouble(Random random, int numberOfDecimalPlaces, Range<Double> constraint) {
    this.numberOfDecimalPlaces = numberOfDecimalPlaces;
    this.random = random;
    this.constraint = constraint;
    this.scale = (Math.pow(SCALE, numberOfDecimalPlaces));
    this.lowestIncrement = 1.0D / scale;

  }

  /**
   * Return any double in the intersection of the specified range and this class
   * own defined range.
   *
   * @param range
   *          the desired range
   * @return a random double
   */
  public double any(final Range<Double> range) {
    Range<Double> r = range.intersection(constraint);

    if (r.isEmpty()) {
      throw new IllegalStateException(String.format(
          "The intersection of the passed in range %s and this class constrained range %s result in a empty range", range, constraint));
    }

    double upperBound = r.hasUpperBound() ? r.upperEndpoint() : 1.0E7D;
    double lowerBound = r.hasLowerBound() ? r.lowerEndpoint() : -1.0E7D;

    if (r.hasUpperBound() && BoundType.CLOSED == r.upperBoundType()) {
      upperBound = upperBound + lowestIncrement;
    }

    if (r.hasLowerBound() && BoundType.OPEN == r.lowerBoundType()) {
      lowerBound = lowerBound + lowestIncrement;
    }

    if(Double.isInfinite(lowerBound * scale)){
      lowerBound = lowerBound / scale;
    }

    if(Double.isInfinite(upperBound * scale)){
      upperBound = upperBound / scale;
    }

    double anyDoubleRange;

    if (lowerBound < 0 && upperBound > 0) {
      // special case
      double lowerRandomDouble = (Math.floor(this.random.nextDouble() * (-lowerBound) * scale) / scale) + lowerBound;
      double upperRandomDouble = (Math.floor(this.random.nextDouble() * upperBound * scale) / scale);
      anyDoubleRange = lowerRandomDouble + upperRandomDouble;

    } else {
      double delta = Math.abs(upperBound - lowerBound);

      anyDoubleRange = (Math.floor(this.random.nextDouble() * delta * scale) / scale) + lowerBound;

    }

    return anyDoubleRange;
  }

  /**
   * Return any double within this class defined double range.
   *
   * @return a random double
   */
  public double any() {
    return any(ALL);
  }

  /**
   * Return any positive double within this class defined double range.
   *
   * @return a random double
   */
  public double anyPositive() {
    return any(EVERY_POSITIVE_INCLUDING_ZERO);
  }

  /**
   * Return any positive double, except zero, within this class defined double
   * range.
   *
   * @return a random double
   */
  public double anyPositiveButZero() {
    return any(EVERY_POSITIVE_BUT_ZERO);
  }

  /**
   * Return any positive double within this class defined double range and
   * having a max value of <code>max</code>.
   *
   * @param max
   *          the maximum
   * @return a random double
   */
  public double anyPositive(double max) {
    return any(Range.closedOpen(0D, max));
  }

  /**
   * Return any positive double, except zero, within this class defined double
   * range and having a max value of <code>max</code>.
   *
   * @param max
   *          the maximum
   * @return a random double
   */
  public double anyPositiveButZero(double max) {
    return any(Range.open(0D, max));
  }

  /**
   * Return any negative double within this class defined double range.
   *
   * @return a random double
   */
  public double anyNegative() {
    return any(EVERY_NEGATIVE_INCLUDING_ZERO);
  }

  /**
   * Return any negative double, except zero, within this class defined double
   * range.
   *
   * @return a random double
   */
  public double anyNegativeButZero() {
    return any(EVERY_NEGATIVE_BUT_ZERO);
  }

  /**
   * Return any negative double within this class defined double range and
   * having a minimum value of <code>min</code>.
   *
   * @param min
   *          the minimum
   * @return a random double
   */
  public double anyNegative(double min) {
    return any(Range.openClosed(min, 0D));
  }

  /**
   * Return any negative double, except zero, within this class defined double
   * range and having a minimum value of <code>min</code>.
   *
   * @param min
   *          the minimum
   * @return a random double
   */
  public double anyNegativeButZero(double min) {
    return any(Range.open(min, 0D));
  }

  /**
   * @param numberOfDecimalPlaces the number of decimals
   * @return a new instance
   */
  public RandomDouble numberOfDecimalPlaces(int numberOfDecimalPlaces) {
    return new RandomDouble(random, numberOfDecimalPlaces, constraint);
  }

  /**
   * Constraint using the intersection of the specified range with this class own range.
   *
   * @param constraint the desired range.
   * @return a new instance
   */
  public RandomDouble constraint(Range<Double> constraint) {
    return new RandomDouble(random, numberOfDecimalPlaces, this.constraint.intersection(constraint));
  }

}
