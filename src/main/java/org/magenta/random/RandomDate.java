package org.magenta.random;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Range;

/**
 * Helper class for {@link Date} generation.
 *
 * @author ngagnon
 *
 */
public class RandomDate {

  private static final Range<Date> ALL = Range.all();
  private static final Range<Date> FROM_1970_TO_2070 = Range.closed(new Date(0), new Date(1000L * 60 * 60 * 24 * 365 * 100));

  private RandomLong longs;

  private long resolutionInMillis;

  private Range<Date> constraint;

  /**
   * Constructs an instance using a resolution of 1 millis and a date range
   * between 1970 and 2070.
   *
   * @param longs
   *          from which moment will be randomly selected.
   */
  public RandomDate(RandomLong longs) {
    this(1L, FROM_1970_TO_2070, longs);
  }

  /**
   * Full constructor.
   *
   * @param resolutionInMillis
   *          resolution of the generated time.
   * @param constraint
   *          the date range from which to pick a date
   * @param longs
   *          the random longs from which moments will be randomly selected.
   */
  public RandomDate(long resolutionInMillis, Range<Date> constraint, RandomLong longs) {
    this.resolutionInMillis = resolutionInMillis;
    this.constraint = constraint;
    this.longs = longs;

  }

  /**
   * Randomly select a date included in this {@link RandomDate} defined range.
   *
   * @return a randomly generated date
   */
  public Date any() {
    return any(ALL);
  }

  /**
   * Randomly select a date within the specified <code>range</code> that
   * intersects with this {@link RandomDate} defined range.
   *
   * @param range
   *          a range
   * @return a randomly generated date
   */
  public Date any(Range<Date> range) {

    Range<Date> r = range.intersection(constraint);

    if (r.isEmpty()) {
      throw new IllegalStateException(String.format(
          "The intersection of the passed in range %s and this class constrained range %s result in a empty range", range, constraint));
    }

    Range<Long> dateRangeInLong;

    if (r.hasLowerBound() && r.hasUpperBound()) {
      dateRangeInLong = Range.range(r.lowerEndpoint().getTime(), r.lowerBoundType(), r.upperEndpoint().getTime(), r.upperBoundType());
    } else if (r.hasLowerBound()) {
      dateRangeInLong = Range.downTo(r.lowerEndpoint().getTime(), r.lowerBoundType());
    } else if (r.hasUpperBound()) {
      dateRangeInLong = Range.upTo(r.upperEndpoint().getTime(), r.upperBoundType());
    } else {
      dateRangeInLong = Range.all();
    }

    long dateInTime = longs.resolution(resolutionInMillis).any(dateRangeInLong);

    Date randomDate = new Date(dateInTime);

    return randomDate;
  }

  /**
   * Randomly select a date in the future (a moment after now) within the
   * specified <code>period</code> of <code>unit</code>.
   *
   * @param period
   *          the period from which to select a date
   * @param unit
   *          the unit of the period
   * @return a randomly generated date
   */
  public Date anyInTheFuture(long period, TimeUnit unit) {
    Date today = new Date();
    Date after = new Date(today.getTime() + (TimeUnit.MILLISECONDS.convert(period, unit)));
    Range<Date> range = Range.closed(today, after);
    return any(range);
  }

  /**
   * Randomly select a date in the past (a moment after now) within the
   * specified <code>period</code> of <code>unit</code>.
   *
   * @param period
   *          the period from which to select a date
   * @param unit
   *          the unit of the period
   * @return a randomly generated date
   */
  public Date anyInThePast(long period, TimeUnit unit) {
    Date today = new Date();
    Date before = new Date(today.getTime() - (TimeUnit.MILLISECONDS.convert(period, unit)));
    Range<Date> range = Range.closed(before, today);
    return any(range);
  }

  /**
   * Randomly select a date after a specified moment within the specified
   * <code>period</code> of <code>unit</code>.
   *
   * @param from
   *          the initial moment
   * @param period
   *          the period from which to select a date
   * @param unit
   *          the unit of the period
   * @return a randomly generated date
   */
  public Date anyInTheNext(Date from, int period, TimeUnit unit) {
    Date after = new Date(from.getTime() + (TimeUnit.MILLISECONDS.convert(period, unit)));
    Range<Date> range = Range.closed(from, after);
    return any(range);
  }

  /**
   * Randomly select a date before a specified moment within the specified
   * <code>period</code> of <code>unit</code>.
   *
   * @param to
   *          the final moment
   * @param period
   *          the period from which to select a date
   * @param unit
   *          the unit of the period
   * @return a randomly generated date
   */
  public Date anyInTheLast(Date to, int period, TimeUnit unit) {
    Date before = new Date(to.getTime() - (TimeUnit.MILLISECONDS.convert(period, unit)));
    Range<Date> range = Range.closed(before, to);
    return any(range);
  }

  /**
   * The resolution in milliseconds of the time to generate. E.G. a value of
   * "1000" will generate random date that are separated by a minimum of 1
   * second.
   *
   * @param resolutionInMillis the resolution
   * @return this instance
   */
  public RandomDate resolution(long resolutionInMillis) {
    return new RandomDate(resolutionInMillis, constraint, longs);
  }

  /**
   * Constraint this instant to the specified range.  The real range will in fact be an intersection of the current range and the one specified.
   *
   * @param constraint the range
   * @return this instance
   */
  public RandomDate constraint(Range<Date> constraint) {
    return new RandomDate(resolutionInMillis, this.constraint.intersection(constraint), longs);
  }

}
