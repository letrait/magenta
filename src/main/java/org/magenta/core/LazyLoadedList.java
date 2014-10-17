package org.magenta.core;

import java.util.AbstractList;
import java.util.List;

import org.magenta.DataStore;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;

/**
 * Implementation of a list that lazyly persist and load data from an underlying
 * datastore as it is requested.
 *
 * @author ngagnon
 *
 * @param <D>
 */
public class LazyLoadedList<D> extends AbstractList<D> implements List<D> {

  private final Supplier<? extends Iterable<D>> generator;
  private final DataStore<D> store;

  private transient volatile List<D> generated;
  private transient boolean[] persistenceFlags;

  /**
   * Default constructor.
   *
   * @param generator
   *          the source of data
   * @param repo
   *          the datasource to which persist data
   */
  public LazyLoadedList(Supplier<? extends Iterable<D>> generator, DataStore<D> store) {
    this.generator = generator;
    this.store = store;
  }

  private List<D> getGenerated() {
    if (generated == null) {
      generated = Lists.newArrayList(generator.get());
      persistenceFlags = new boolean[generated.size()];
    }
    return generated;
  }

  @Override
  public int size() {
    return getGenerated().size();
  }

  @Override
  public boolean isEmpty() {
    return getGenerated().isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return getGenerated().contains(o);
  }

  @Override
  public D get(int index) {
    D target = getGenerated().get(index);
    boolean persisted = persistenceFlags[index];
    D result;
    if (!persisted) {
      result = store.persist(target);
      persistenceFlags[index] = true;
    } else {
      result = store.retrieve(target);
    }
    return result;
  }
}
