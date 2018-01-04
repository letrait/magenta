package org.magenta.core;

import org.magenta.DataSet;
import org.magenta.DataStore;
import org.magenta.random.FluentRandom;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;


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
  private Supplier<DataStore<D>> store;

  /**
   * Default constructor.
   *
   * @param dataset the data set to persist
   * @param store the store to use for persistence
   * @param randomizer the randomizer
   */
  public PersistentDataSet(DataSet<D> dataset, Supplier<DataStore<D>> store, FluentRandom randomizer) {
    super(dataset.getType(), randomizer);
    Preconditions.checkArgument(dataset.isConstant(),"Persistent dataset does not support non constant dataset");
    this.source = dataset;
    this.store = store;
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
  public boolean isConstant() {
    return true;
  }

  @Override
  public DataSet<D> toTransient() {
    return source;
  }

  @Override
  public PersistentDataSet<D> persist() {
    this.persistedData.flagAllAsNotPersisted();
    return this;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("type", getType()).toString();
  }

}
