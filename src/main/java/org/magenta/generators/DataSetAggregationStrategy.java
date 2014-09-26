package org.magenta.generators;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.magenta.Fixture;
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
  public Iterable<D> generate(final Fixture<? extends S> dataDomain) {

    Function<DataKey<? extends D>, DataSet<? extends D>> getDataSetFromKey = createGetDataSetFunction(dataDomain);

    return dataDomain.getRandomizer().mix(FluentIterable.from(keys).transform(getDataSetFromKey).transform(getIterableFromDataSet));

  }

  @Override
  public Iterable<D> generate(int numberOfElements, final Fixture<? extends S> dataDomain) {

    Function<DataKey<? extends D>, DataSet<? extends D>> getDataSetFromKey = createGetDataSetFunction(dataDomain);

    return Iterables.limit(dataDomain.getRandomizer().mix(FluentIterable.from(keys).transform(getDataSetFromKey).transform(getIterableFromDataSet)),
        numberOfElements);
  }

  private Function<DataKey<? extends D>, DataSet<? extends D>> createGetDataSetFunction(final Fixture<? extends S> dataDomain) {
    Function<DataKey<? extends D>, DataSet<? extends D>> toDataSet = new Function<DataKey<? extends D>, DataSet<? extends D>>() {

      private Map<DataKey,CacheEntry> cache = new HashMap<DataKey,CacheEntry>();


      @Override
      public DataSet<? extends D> apply(DataKey<? extends D> key) {

        CacheEntry entry = cache.get(key);

        if(entry !=null && dataDomain.getVersion() == entry.getLastVersion()){
          return (DataSet)entry.getCached();
        }

        entry = new CacheEntry( dataDomain.dataset(key), dataDomain.getVersion());

        cache.put(key, entry);

        return (DataSet)entry.getCached();
      }
    };
    return toDataSet;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Iterable<DataKey<?>> getModifiedDataSet() {
    return Collections.EMPTY_LIST;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    sb.append(DataSetAggregationStrategy.class.getSimpleName()).append(" materialized from ");
    for(DataKey k:this.keys){
      sb.append(k).append(',');
    }

    return sb.toString();

  }

  private static class CacheEntry{

    private final DataSet<?> cached;
    private final int lastVersion;

    public CacheEntry(DataSet<?> cached, int lastVersion) {
      super();
      this.cached = cached;
      this.lastVersion = lastVersion;
    }

    public DataSet<?> getCached() {
      return cached;
    }
    public int getLastVersion() {
      return lastVersion;
    }



  }

}
