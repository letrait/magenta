package org.magenta.core;

import static com.google.common.base.Predicates.in;
import static com.google.common.base.Predicates.not;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.magenta.DataSet;
import org.magenta.DataSpecification;
import org.magenta.FixtureFactory;
import org.magenta.GenerationStrategy;
import org.magenta.Generator;
import org.magenta.generators.TransformedStrategy;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Implementation of a {@link Generator} that delegates to a
 * {@link GenerationStrategy} to actually generate data.
 *
 * @author ngagnon
 *
 * @param <T>
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class GeneratorImpl<T, S extends DataSpecification> implements Generator<T> {

  private final FixtureFactory<S> dataSetMap;
  private final GenerationStrategy<T, ? super S> strategy;
  private final Class<T> type;
  private static final int MAX_COUNT = 1000;

  /**
   * Default constructor.
   *
   * @param dataSetMap
   *          the dataSetMap to which this generator belongs
   * @param strategy
   *          the strategy to use to generate data.
   * @param type
   *          the type of generated data.
   */
  public GeneratorImpl(FixtureFactory dataSetMap, GenerationStrategy<T, ? super S> strategy, Class<T> type) {
    this.dataSetMap = dataSetMap;
    this.strategy = strategy;
    this.type = type;
  }

  @Override
  public Generator<T> restrictTo(Object... objects) {

    FixtureFactory child = this.dataSetMap.newNode("restricted " + this.dataSetMap.getName());

    RestrictionHelper.applyRestrictions(child, objects);

    return new GeneratorImpl(child, this.strategy, type);
  }

  @Override
  public T any() {
    // TODO this implementation always generate the "first" element, this may
    // prevent the result from being truly generated
    return strategy.generate(1, this.dataSetMap).iterator().next();
  }

  @Override
  public T any(Predicate<? super T> filter) {
    int i = 0;
    T candidate = null;

    do {
      candidate = any();
    } while (i++ < MAX_COUNT && filter.apply(candidate));

    if (i >= MAX_COUNT) {
      throw new IllegalStateException(String.format("reach maximum count while trying to generate a %s that matches the filter %s", type, filter));
    }

    return candidate;
  }

  @Override
  public Iterable<T> get() {
    return strategy.generate(this.dataSetMap);
  }

  @Override
  public T[] array() {
    return Iterables.toArray(get(), this.type);
  }

  @Override
  public T[] array(int size) {
    return Iterables.toArray(list(size), this.type);
  }

  @Override
  public T[] randomArray() {
    return Iterables.toArray(randomList(), this.type);
  }

  @Override
  public T[] randomArray(int size) {
    return Iterables.toArray(randomList(size), this.type);
  }

  @Override
  public List<T> list() {
    return Lists.newArrayList(strategy.generate(this.dataSetMap));
  }

  @Override
  public List<T> list(int size) {
    return Lists.newArrayList(strategy.generate(size, this.dataSetMap));
  }

  @Override
  public List<T> randomList() {
    return list();
  }

  @Override
  public List<T> randomList(int size) {
    return list(size);
  }

  @Override
  public Set<T> set() {
    return Sets.newHashSet(strategy.generate(this.dataSetMap));
  }

  @Override
  public Set<T> set(int size) {
    return Sets.newHashSet(strategy.generate(size, this.dataSetMap));
  }

  @Override
  public DataSet<T> subset(final int size) {
    return new GenericDataSet<T>(Suppliers.memoize(new Supplier<Iterable<T>>() {
      @Override
      public Iterable<T> get() {
        return strategy.generate(size, dataSetMap);
      }
    }), this.type, dataSetMap.getRandomizer());
  }

  @Override
  public Generator<T> filter(final Predicate<? super T> filter) {
    return new GeneratorImpl<T, S>(dataSetMap, new TransformedStrategy<T, T, S>(strategy, filter, Functions.<T> identity()), type);
  }

  @Override
  public Generator<T> without(T... items) {
    return filter(not(in(Arrays.asList(items))));
  }

  @Override
  public Generator<T> without(Collection<T> items) {
    return filter(not(in(items)));
  }

  @Override
  public <X> Generator<X> transform(final Function<? super T, X> function, Class<X> newType) {
    return new GeneratorImpl<X, S>(dataSetMap, new TransformedStrategy<>(strategy, Predicates.alwaysTrue(), function), newType);
  }

  @Override
  public <X> DataSet<X> cast(final Class<X> type) {
    return transform(new Function<T,X>(){

      @Override
      public X apply(T input) {
       return type.cast(input);
      }

    }, type);
  }

  /*
   * @Override public Generator<T> process(final Function<? super T, Void>
   * processor) { return new GeneratorImpl<T,S>(dataSetMap,new
   * TransformedStrategy<>(strategy, Predicates.alwaysTrue(), new Function<T,
   * T>() {
   *
   * @Override public T apply(T input) { processor.apply(input); return input; }
   * }),type); }
   */

  @Override
  public T link(Object o) {
    throw new UnsupportedOperationException("Linking objects is not supported on strategy");
  }

  @Override
  public <L> Iterable<L> reverseLink(Class<L> type, T referred) {
    throw new UnsupportedOperationException("reverseLink objects is not supported on strategy");
  }

  @Override
  public Class<T> getType() {
    return this.type;
  }

  @Override
  public boolean isGenerated() {
    return true;
  }

  @Override
  public boolean isPersistent() {
    return false;
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public boolean isConstant() {
    return false;
  }

  @Override
  public DataSet<T> toTransient() {
    return this;
  }

  @Override
  public DataSet<T> persist() {
    throw new UnsupportedOperationException("This dataset is not persistent:"+this.toString());
  }

  @Override
  public DataSet<T> load() {
    return this;
  }



}
