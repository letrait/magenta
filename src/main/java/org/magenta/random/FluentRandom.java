package org.magenta.random;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;

/**
 * An instance of this class allow random generation of various primitives ({@code integers, shorts, longs, double}) and
 * base types ({@code dates and strings}).
 *
 * @author ngagnon
 *
 */
public class FluentRandom {

  private static final int DEFAULT_DOUBLE_NUMBER_OF_DECIMAL_PLACES = 8;

  private Random random;
  private RandomInteger integers;
  private RandomShort shorts;
  private RandomDouble doubles;
  private RandomLong longs;
  private RandomString strings;
  private RandomDate dates;

  FluentRandom(){
    //for cglib
  }

  FluentRandom(Random random) {
    // use suppliers?
    this.random = random;
    this.longs = new RandomLong(random);
    this.integers = new RandomInteger(random);
    this.shorts = new RandomShort(random);
    this.doubles = new RandomDouble(random, DEFAULT_DOUBLE_NUMBER_OF_DECIMAL_PLACES);
    this.strings = strings("qwertyuiopasdfghjklzxcvbnm0123456789");
    this.dates = new RandomDate(longs);
  }

  private static final Supplier<FluentRandom> SINGLETON = Suppliers.memoize(new Supplier<FluentRandom>() {

    @Override
    public FluentRandom get() {
      return new FluentRandom(Helper.initWithDefaultRandom());
    }

  });

  /**
   * @return the singleton
   */
  public static FluentRandom singleton() {
    return SINGLETON.get();
  }

  /**
   * Return a new instance of {@code Randoms} using the specified {@code random}.
   *
   * @param random the random
   * @return a new instance
   */
  public static FluentRandom get(Random random) {
    return new FluentRandom(random);
  }



  /**
   * Get the random.
   * @return the random
   */
  public Random getRandom() {
    return this.random;
  }



  /**
   * @return date generator
   */
  public RandomDate dates() {
    return this.dates;
  }

  /**
   * @return string generator
   */
  public RandomString strings() {
    return this.strings;
  }

  /**
   * @param alphabet the alphabet from which to generate strings
   * @return string generator
   */
  public RandomString strings(String alphabet) {
    return new RandomString(alphabet, integers);
  }

  /**
   * @return a integer generator
   */
  public RandomInteger integers() {
    return this.integers;
  }

  /**
   * @param constrained the range of possible values.
   * @return a integer generator
   */
  public RandomInteger integers(Range<Integer> constrained) {
    return new RandomInteger(random, constrained, 1);
  }

  /**
   * @return a long generator.
   */
  public RandomLong longs() {
    return this.longs;
  }

  /**
   * @param constrained the range of possible values
   * @return a long generator
   */
  public RandomLong longs(Range<Long> constrained) {
    return new RandomLong(random, constrained, 1);
  }

  /**
   * @return a short generator
   */
  public RandomShort shorts() {
    return this.shorts;
  }

  /**
   * @param constrained the range of possible values
   * @return a short generators
   */
  public RandomShort shorts(Range<Short> constrained) {
    return new RandomShort(random, constrained, (short) 1);
  }

  /**
   * @return a double generator.
   */
  public RandomDouble doubles() {
    return this.doubles;
  }

  /**
   * @param numberOfDecimalPlaces number of decimal places in the generated numbers
   * @return a double generator
   */
  public RandomDouble doubles(int numberOfDecimalPlaces) {
    return new RandomDouble(random, numberOfDecimalPlaces);
  }

  /**
   * @param numberOfDecimalPlaces number of decimal places in the generated numbers
   * @param constrained the range of possible values
   * @return a double generator
   */
  public RandomDouble doubles(int numberOfDecimalPlaces, Range<Double> constrained) {
    return new RandomDouble(random, numberOfDecimalPlaces, constrained);
  }

  /**
   * @param values the list of values to pick from
   * @param <E> the type of value
   * @return a list picker
   *
   */
  @SafeVarargs
  public final <E> RandomList<E> array(E... values) {
    return new RandomList<>(random, integers(), Arrays.asList(values));
  }

  /**
   * @param clazz the type of enum
   * @param <E> type of enum
   * @return a enum picker
   */
  public final <E extends Enum<E>> RandomList<E> enums(Class<E> clazz) {

    List<E> enums = Arrays.asList(clazz.getEnumConstants());

    return new RandomList<>(random, integers(), enums);
  }

  /**
   * @param values the collection of values to pick from
   * @param <E> the type of value
   * @return a list picker
   */
  public <E> RandomList<E> iterable(Iterable<E> values) {
    return new RandomList<>(random, integers(), Lists.newArrayList(values));
  }

  /**
   * Return a random iterator that will iterate from the specified collection of iterable randomly.
   *
   * @param iterables the iterables
   * @param <E> the type of value
   * @return a mixed iterable
   */
  public <E> Iterable<E> mix(Iterable<Iterable<? extends E>> iterables) {
    return new MixedIterable<>(iterables, this);
  }

  private static class Helper {
    private static final Logger LOG = LoggerFactory.getLogger(FluentRandom.class);

    public static final String RANDOM_SEED_SYSTEM_PROPERTY_KEY = "magenta.random.seed";

    // <--
    // cut&paste from the Random class
    private static final AtomicLong SEED_UNIQUIFIER = new AtomicLong(8682522807148012L);

    private static final long M = 181783497276652981L;

    private static long seedUniquifier() {
      // L'Ecuyer, "Tables of Linear Congruential Generators of
      // Different Sizes and Good Lattice Structure", 1999
      for (;;) {
        long current = SEED_UNIQUIFIER.get();
        long next = current * M;
        if (SEED_UNIQUIFIER.compareAndSet(current, next)) {
          return next;
        }
      }
    }

    // -->
    // end cut&paste

    private static Random initWithDefaultRandom() {
      LOG.info("------------------------------------------------------------------------");
      LOG.info("MAGENTA RANDOM initialization");

      String seedValue = System.getProperty(RANDOM_SEED_SYSTEM_PROPERTY_KEY);
      Long seed = null;
      if (seedValue != null) {
        try {

          LOG.info("The seed value read from the system property {} is {}", RANDOM_SEED_SYSTEM_PROPERTY_KEY, seedValue);
          seed = Long.parseLong(seedValue);

        } catch (Throwable t) {
          // ignore;
          LOG.info("cannot parse this value to a valid long");
        }
      } else {
        LOG.info(
            "No seed found in system properties, a new one will be generated. You can set a fixed seed for Random by setting the System Property \"{}\" to the desired value.",
            RANDOM_SEED_SYSTEM_PROPERTY_KEY);
      }

      if (seed == null) {
        seed = seedUniquifier() ^ System.nanoTime();
      }

      LOG.info("The seed used by magenta for random is {}", seed);
      LOG.info("------------------------------------------------------------------------");
      Random random = new Random(seed);

      return random;

    }
  }

}
