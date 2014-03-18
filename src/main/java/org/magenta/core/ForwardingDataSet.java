package org.magenta.core;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.magenta.DataSet;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;

/**
 * Implementation of a forwarding data set that can be used to implement the
 * Decorator pattern over existing {@link DataSet}.
 *
 * @author ngagnon
 *
 * @param <D>
 */

public class ForwardingDataSet<D> implements DataSet<D> {

  private final DataSet<D> delegate;


  /**
   * @param delegate the delegate
   */
  public ForwardingDataSet(DataSet<D> delegate) {
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
    if (!delegate.equals(other)) {
      return false;
    }
    return true;
  }

  @Override
  public Class<D> getType() {
    return delegate.getType();
  }

  @Override
  public D[] array() {
    return delegate.array();
  }

  @Override
  public D[] array(int size) {
    return delegate.array(size);
  }

  @Override
  public D[] randomArray() {
    return delegate.randomArray();
  }

  @Override
  public D[] randomArray(int size) {
    return delegate.randomArray(size);
  }

  @Override
  public Iterable<D> get() {
    return delegate.get();
  }

  @Override
  public List<D> list() {
    return delegate.list();
  }

  @Override
  public List<D> list(int size) {
    return delegate.list(size);
  }

  @Override
  public List<D> randomList() {
    return delegate.randomList();
  }

  @Override
  public List<D> randomList(int size) {
    return delegate.randomList(size);
  }

  @Override
  public Set<D> set() {
    return delegate.set();
  }

  @Override
  public Set<D> set(int size) {
    return delegate.set(size);
  }

  @Override
  public DataSet<D> subset(int size) {
    return delegate.subset(size);
  }

  @Override
  public DataSet<D> filter(Predicate<? super D> filter) {
    return delegate.filter(filter);
  }

  @Override
  public <X> DataSet<X> transform(Function<? super D, X> function, Class<X> transformedType) {
    return delegate.transform(function, transformedType);
  }
  @Override
  public <S> DataSet<S> cast(Class<S> type) {
    return delegate.cast(type);
  }

  @Override
  public D any() {
    return delegate.any();
  }

  @Override
  public D link(Object o) {
    return delegate.link(o);
  }

  @Override
  public <L> Iterable<L> reverseLink(Class<L> type, D referred) {
    return delegate.reverseLink(type, referred);
  }

  @Override
  @SafeVarargs
  public final DataSet<D> without(D... items) {
    return delegate.without(items);
  }

  @Override
  public boolean isGenerated() {
    return delegate.isGenerated();
  }

  @Override
  public DataSet<D> without(Collection<D> items) {
    return delegate.without(items);
  }

  @Override
  public boolean isEmpty() {
    return delegate.isEmpty();
  }

  /*
   * @Override public DataSet<D> process(Function<? super D, Void> processor) {
   * return delegate.process(processor); }
   */

  @Override
  public D any(Predicate<? super D> filter) {
    return delegate.any(filter);
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this).add("delegate", delegate).toString();
  }


}
