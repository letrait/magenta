package org.magenta.core;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.magenta.DataSet;

/**
 * Implementation of a forwarding data set that can be used to implement the
 * Decorator pattern over existing {@link DataSet}.
 *
 * @author ngagnon
 *
 * @param <D>
 */

public class ForwardingDataSet<D> implements DataSet<D> {

  private final Supplier<DataSet<D>> delegate;


  /**
   * @param delegate the delegate
   */
  public ForwardingDataSet(Supplier<DataSet<D>> delegate) {
    this.delegate = delegate;
  }
  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((delegate == null) ? 0 : delegate.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof DataSet)) {
      return false;
    }
    DataSet<?> other = (DataSet<?>) obj;
    if (!delegate.get().equals(other)) {
      return false;
    }
    return true;
  }

  @Override
  public Class<D> getType() {
    return delegate.get().getType();
  }

  @Override
  public D[] array() {
    return delegate.get().array();
  }

  @Override
  public D[] array(int size) {
    return delegate.get().array(size);
  }

  @Override
  public D[] randomArray() {
    return delegate.get().randomArray();
  }

  @Override
  public D[] randomArray(int size) {
    return delegate.get().randomArray(size);
  }

  @Override
  public Iterable<D> get() {
    return delegate.get().get();
  }

  @Override
  public List<D> list() {
    return delegate.get().list();
  }

  @Override
  public List<D> list(int size) {
    return delegate.get().list(size);
  }

  @Override
  public List<D> randomList() {
    return delegate.get().randomList();
  }

  @Override
  public List<D> randomList(int size) {
    return delegate.get().randomList(size);
  }

  @Override
  public Set<D> set() {
    return delegate.get().set();
  }

  @Override
  public Set<D> set(int size) {
    return delegate.get().set(size);
  }

  @Override
  public DataSet<D> subset(int size) {
    return delegate.get().subset(size);
  }

  @Override
  public DataSet<D> filter(Predicate<? super D> filter) {
    return delegate.get().filter(filter);
  }

  @Override
  public <X> DataSet<X> transform(Function<? super D, X> function, Class<X> transformedType) {
    return delegate.get().transform(function, transformedType);
  }
  @Override
  public <S> DataSet<S> cast(Class<S> type) {
    return delegate.get().cast(type);
  }

  @Override
  public D any() {
    return delegate.get().any();
  }

  @Override
  public D link(Object o) {
    return delegate.get().link(o);
  }

  @Override
  public <L> Iterable<L> reverseLink(Class<L> type, D referred) {
    return delegate.get().reverseLink(type, referred);
  }

  @Override
  @SafeVarargs
  public final DataSet<D> without(D... items) {
    return delegate.get().without(items);
  }

  @Override
  public boolean isGenerated() {
    return delegate.get().isGenerated();
  }

  @Override
  public boolean isPersistent() {
    return delegate.get().isPersistent();
  }

  @Override
  public DataSet<D> toTransient() {
    return delegate.get().toTransient();
  }

  @Override
  public DataSet<D> without(Collection<D> items) {
    return delegate.get().without(items);
  }

  @Override
  public boolean isEmpty() {
    return delegate.get().isEmpty();
  }

  @Override
  public boolean isConstant() {
    return delegate.get().isConstant();
  }

  /*
   * @Override public DataSet<D> process(Function<? super D, Void> processor) {
   * return delegate.process(processor); }
   */

  @Override
  public D any(Predicate<? super D> filter) {
    return delegate.get().any(filter);
  }

  @Override
  public DataSet<D> persist() {

    return delegate.get().persist();
  }
  @Override
  public DataSet<D> load() {
    return delegate.get().load();
  }



}
