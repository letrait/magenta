package org.magenta.random;

import java.util.Random;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Range;

public interface RandomBuilder {

  public abstract <E> Iterable<E> mix(Iterable<Iterable<? extends E>> iterables);

  public abstract <E> RandomList<E> iterable(Iterable<E> values);

  public abstract <E extends Enum<E>> RandomList<E> enums(Class<E> clazz);

  public abstract <E> RandomList<E> array(E... values);

  public abstract RandomDouble doubles(int numberOfDecimalPlaces, Range<Double> constrained);

  public abstract RandomDouble doubles(int numberOfDecimalPlaces);

  public abstract RandomDouble doubles();

  public abstract RandomShort shorts(Range<Short> constrained);

  public abstract RandomShort shorts();

  public abstract RandomLong longs(Range<Long> constrained);

  public abstract RandomLong longs();

  public abstract RandomInteger integers(Range<Integer> constrained);

  public abstract RandomInteger integers();

  public abstract RandomString strings(String alphabet);

  public abstract RandomString strings();

  public abstract RandomDate dates();

  public abstract Random getRandom();

  public static class PROVIDER {

    private static final Supplier<RandomBuilder> SINGLETON = Suppliers.memoize(new Supplier<RandomBuilder>() {

      @Override
      public RandomBuilder get() {
        return new RandomBuilderImpl();
      }

    });

    /**
     * @return the singleton
     */
    public static RandomBuilder singleton() {
      return SINGLETON.get();
    }

    /**
     * Return a new instance of {@code Randoms} using the specified {@code random}.
     *
     * @param random the random
     * @return a new instance
     */
    public static RandomBuilder get(Random random) {
      return new RandomBuilderImpl(random);
    }
  }


}