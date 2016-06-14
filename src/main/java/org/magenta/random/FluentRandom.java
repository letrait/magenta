package org.magenta.random;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.assertj.core.util.Lists;
import org.magenta.DataSupplier;
import org.magenta.core.data.supplier.StaticDataSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Range;
import com.google.common.reflect.TypeToken;

/**
 * An instance of this class allow random generation of various primitives ({@code integers, shorts, longs, double}) and
 * base types ({@code dates and strings}).
 *
 * @author ngagnon
 *
 */
public class FluentRandom {

  private static final int DEFAULT_DOUBLE_NUMBER_OF_DECIMAL_PLACES = 8;

  private static FluentRandom INSTANCE = new FluentRandom(Helper.initWithDefaultRandom());

  private Random random;
  private RandomInteger integers;
  private RandomShort shorts;
  private RandomDouble doubles;
  private RandomLong longs;
  private RandomString strings;
  private RandomDate dates;


  private FluentRandom(Random random){
    this.random = random;
    this.integers= new RandomInteger(random);
    this.shorts= new RandomShort(random);
    this.doubles=new RandomDouble(random, DEFAULT_DOUBLE_NUMBER_OF_DECIMAL_PLACES);
    this.longs = new RandomLong(random);
    this.strings = new RandomString("qwertyuiopasdfghjklzxcvbnm0123456789",  new RandomInteger(random));
    this.dates = new RandomDate(new RandomLong(random));
  }


  /**
   * Get the random.
   * @return the random
   */
  public static Random getRandom() {
    return INSTANCE.random;
  }

  public static void setRandom(Random r){

    INSTANCE = new FluentRandom(r);
  }

  /**
   * @return date generator
   */
  public static RandomDate dates() {
    return INSTANCE.dates;
  }

  /**
   * @return string generator
   */
  public static RandomString strings() {
    return INSTANCE.strings;
  }

  /**
   * @param alphabet the alphabet from which to generate strings
   * @return string generator
   */
  public static RandomString strings(String alphabet) {
    return new RandomString(alphabet, INSTANCE.integers);
  }

  /**
   * @return a integer generator
   */
  public static RandomInteger integers() {
    return INSTANCE.integers;
  }

  /**
   * @param constrained the range of possible values.
   * @return a integer generator
   */
  public static RandomInteger integers(Range<Integer> constrained) {
    return new RandomInteger(INSTANCE.random, constrained, 1);
  }

  /**
   * @return a long generator.
   */
  public static RandomLong longs() {
    return INSTANCE.longs;
  }

  /**
   * @param constrained the range of possible values
   * @return a long generator
   */
  public static RandomLong longs(Range<Long> constrained) {
    return new RandomLong(INSTANCE.random, constrained, 1);
  }

  /**
   * @return a short generator
   */
  public static RandomShort shorts() {
    return INSTANCE.shorts;
  }

  /**
   * @param constrained the range of possible values
   * @return a short generators
   */
  public static RandomShort shorts(Range<Short> constrained) {
    return new RandomShort(INSTANCE.random, constrained, (short) 1);
  }

  /**
   * @return a double generator.
   */
  public static RandomDouble doubles() {
    return INSTANCE.doubles;
  }

  /**
   * @param numberOfDecimalPlaces number of decimal places in the generated numbers
   * @return a double generator
   */
  public static RandomDouble doubles(int numberOfDecimalPlaces) {
    return new RandomDouble(INSTANCE.random, numberOfDecimalPlaces);
  }

  /**
   * @param numberOfDecimalPlaces number of decimal places in the generated numbers
   * @param constrained the range of possible values
   * @return a double generator
   */
  public static RandomDouble doubles(int numberOfDecimalPlaces, Range<Double> constrained) {
    return new RandomDouble(INSTANCE.random, numberOfDecimalPlaces, constrained);
  }

  /**
   * @param values the list of values to pick from
   * @param <E> the type of value
   * @return a list picker
   *
   */
  @SafeVarargs
  public final static <E> RandomList<E> array(E... values) {
    return new RandomList<E>(INSTANCE.random, integers(), new StaticDataSupplier(Lists.newArrayList(values), TypeToken.of(values[0].getClass())));
  }

  /**
   * @param clazz the type of enum
   * @param <E> type of enum
   * @return a enum picker
   */
  public final static <E extends Enum<E>> RandomList<E> enums(Class<E> clazz) {

    List<E> enums = Arrays.asList(clazz.getEnumConstants());

    return new RandomList<>(INSTANCE.random, integers(),  new StaticDataSupplier<E>(enums, TypeToken.of(clazz)));
  }

  /**
   * @param values the collection of values to pick from
   * @param <E> the type of value
   * @return a list picker
   */
  public static <E> RandomList<E> iterable(DataSupplier<E> values) {
    return new RandomList<>(INSTANCE.random, integers(), values);
  }

  /**
   * @param values the collection of values to pick from
   * @param <E> the type of value
   * @return a list picker
   */
  public static <E> RandomList<E> iterable(Iterable<E> values) {
    return new RandomList<E>(INSTANCE.random, integers(), new StaticDataSupplier(Lists.newArrayList(values), TypeToken.of(values.iterator().next().getClass())));
  }

  /**
   * Return a random iterator that will iterate from the specified collection of iterable randomly.
   *
   * @param iterables the iterables
   * @param <E> the type of value
   * @return a mixed iterable
   */
  public static <E> Iterable<E> mix(Iterable<? extends Iterable<? extends E>> iterables) {
    return new MixedIterable<>(iterables);
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

          LOG.info("The seed value read from the system property {}={}", RANDOM_SEED_SYSTEM_PROPERTY_KEY, seedValue);
          seed = Long.parseLong(seedValue);

        } catch (Throwable t) {
          // ignore;
          LOG.info("cannot parse this value to a valid long");
        }
      }

      if (seed == null) {
        seed = seedUniquifier() ^ System.nanoTime();
      }

      LOG.info("The seed used by magenta for random is {}={}", RANDOM_SEED_SYSTEM_PROPERTY_KEY, seed);
      LOG.info("------------------------------------------------------------------------");
      Random random = new Random(seed);

      return random;

    }
  }

}
