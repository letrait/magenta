package org.magenta.core;

import org.magenta.DataSet;
import org.magenta.DataStore;
import org.magenta.random.Randoms;

/**
 * Implementation of {@link DataSet} that persist data.
 *
 * @author ngagnon
 *
 * @param <D>
 */
public class PersistentDataSet<D> extends AbstractDataSet<D> {

  private LazyLoadedList<D> persistedData;
  private DataSet<D> source;

  /**
   * Default constructor.
   *
   * @param dataset the data set to persist
   * @param store the store to use for persistence
   * @param randomizer the randomizer
   */
  public PersistentDataSet(DataSet<D> dataset, DataStore<D> store, Randoms randomizer) {
    super(dataset.getType(), randomizer);
    this.source = dataset;
    this.persistedData = new LazyLoadedList<>(dataset, store);
  }

  @Override
  public boolean isGenerated() {
    return source.isGenerated();
  }

  @Override
  public Iterable<D> get() {
    return this.persistedData;
  }

}
