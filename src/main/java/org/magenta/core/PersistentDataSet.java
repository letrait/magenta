package org.magenta.core;

import org.magenta.DataSet;
import org.magenta.DataStore;
import org.magenta.random.FluentRandom;

import com.google.common.base.Objects;

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
  private DataStore<D> store;

  /**
   * Default constructor.
   *
   * @param dataset the data set to persist
   * @param store the store to use for persistence
   * @param randomizer the randomizer
   */
  public PersistentDataSet(DataSet<D> dataset, DataStore<D> store, FluentRandom randomizer) {
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

  @Override
  public boolean isPersistent() {
    return true;
  }

  @Override
  public DataSet<D> toTransient() {
    return source;
  }

  @Override
  public PersistentDataSet<D> persist() {
    this.persistedData = new LazyLoadedList<>(source, store);
    return this;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this).add("type", getType()).toString();
  }

}
