package org.magenta.generators;

import java.util.Arrays;
import java.util.Collections;

import org.magenta.DataDomain;
import org.magenta.DataKey;
import org.magenta.DataSet;
import org.magenta.DataSpecification;
import org.magenta.GenerationStrategy;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;

/**
 * This strategy generates data from existing dataset and aggregate them as it
 * was a dataset on this own. As it uses datasets and not generators, the data
 * returned by this generator will therefore be the same as getting each dataset
 * referenced by this strategy <code>keys</code> and grouping all the resulting
 * data into one collection.
 *
 * TODO : example code
 *
 * @author normand
 *
 * @param <D>
 * @param <S>
 */
public class DataSetAggregationStrategy<D, S extends DataSpecification> implements GenerationStrategy<D, S> {

  private Iterable<DataKey<? extends D>> keys;

  private Function<DataSet<? extends D>, Iterable<? extends D>> getIterableFromDataSet = new Function<DataSet<? extends D>, Iterable<? extends D>>() {
    @Override
    public Iterable<? extends D> apply(DataSet<? extends D> ds) {
      return ds.get();
    }
  };

  /**
   * Default constructor.
   *
   * @param keys
   *          the data set keys to aggregate.
   */
  public DataSetAggregationStrategy(Iterable<DataKey<? extends D>> keys) {
    this.keys = keys;
  }

  /**
   * Default constructor.
   *
   * @param keys
   *          the data set keys to aggregate.
   */
  @SafeVarargs
  public DataSetAggregationStrategy(DataKey<? extends D>... keys) {
    this.keys = Arrays.asList(keys);
  }

  @Override
  public Iterable<D> generate(final DataDomain<? extends S> dataDomain) {

    Function<DataKey<? extends D>, DataSet<? extends D>> getDataSetFromKey = createGetDataSetFunction(dataDomain);

    return dataDomain.getRandomizer().mix(FluentIterable.from(keys).transform(getDataSetFromKey).transform(getIterableFromDataSet));

  }

  @Override
  public Iterable<D> generate(int numberOfElements, final DataDomain<? extends S> dataDomain) {

    Function<DataKey<? extends D>, DataSet<? extends D>> getDataSetFromKey = createGetDataSetFunction(dataDomain);

    return Iterables.limit(dataDomain.getRandomizer().mix(FluentIterable.from(keys).transform(getDataSetFromKey).transform(getIterableFromDataSet)),
        numberOfElements);
  }

  private Function<DataKey<? extends D>, DataSet<? extends D>> createGetDataSetFunction(final DataDomain<? extends S> dataDomain) {
    Function<DataKey<? extends D>, DataSet<? extends D>> toDataSet = new Function<DataKey<? extends D>, DataSet<? extends D>>() {
      @Override
      public DataSet<? extends D> apply(DataKey<? extends D> key) {
        return dataDomain.dataset(key);
      }
    };
    return toDataSet;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Iterable<DataKey<?>> getModifiedDataSet() {
    return Collections.EMPTY_LIST;
  }

}
