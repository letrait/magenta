package org.magenta.core;

import java.util.AbstractList;
import java.util.List;

import org.magenta.DataStore;

import com.google.common.base.Preconditions;
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
  private final Supplier<DataStore<D>> store;

  private transient volatile List<D> generated;
  private transient boolean[] persistenceFlags;

  /**
   * Default constructor.
   *
   * @param generator
   *          the source of data
   * @param store
   *          the datasource to which persist data
   */
  public LazyLoadedList(Supplier<? extends Iterable<D>> generator, Supplier<DataStore<D>> store) {
    this.generator = generator;
    this.store = store;
  }

  private List<D> getGenerated() {
    if (generated == null) {
      List<D> g = Lists.newArrayList(generator.get());
      if(generated == null){
        generated = g;
        persistenceFlags = new boolean[generated.size()];
      }

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

    DataStore<D> s = Preconditions.checkNotNull(store.get());

 
      if (!persisted) {
        result = s.persist(target);
        persistenceFlags[index] = true;
      } else {
        result = s.retrieve(target);
      }

      getGenerated().set(index, result);

    


    return result;
  }

  public void flagAllAsNotPersisted(){
    if(generated != null ) {
      persistenceFlags = new boolean[generated.size()];
    }
  }
}
