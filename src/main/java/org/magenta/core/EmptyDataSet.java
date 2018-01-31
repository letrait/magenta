package org.magenta.core;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import org.magenta.DataSet;

import com.google.common.collect.Iterables;

/**
 * Implementation of a {@link DataSet} that is empty.
 *
 * @author ngagnon
 *
 * @param <D>
 */
public class EmptyDataSet<D> implements DataSet<D> {

  private Class<D> type;

  /**
   * Default constructor.
   * @param type this dataset type
   */
  private EmptyDataSet(Class<D> type) {
    this.type = type;
  }

  /**
   * Factory method.
   *
   * @param type the type of data
   * @param <D> the type of data
   * @return an empty data set instance
   */
  public static <D> EmptyDataSet<D> ofType(Class<D> type) {
    return new EmptyDataSet<>(type);
  }

  @Override
  public boolean isGenerated() {
    return false;
  }

  @Override
  public boolean isPersistent() {
    return false;
  }

  @Override
  public boolean isConstant() {
    return true;
  }

  @Override
  public Iterable<D> get() {
    return Collections.emptyList();
  }

  @Override
  public Class<D> getType() {
    return type;
  }

  @Override
  public D[] array() {
    return Iterables.toArray(get(), type);
  }

  @Override
  public D[] array(int size) {
    return Iterables.toArray(get(), type);
  }

  @Override
  public D[] randomArray() {
    return Iterables.toArray(get(), type);
  }

  @Override
  public D[] randomArray(int size) {
    return Iterables.toArray(get(), type);
  }

  @Override
  public List<D> list() {
    return Collections.emptyList();
  }

  @Override
  public List<D> list(int size) {
    return Collections.emptyList();
  }

  @Override
  public List<D> randomList() {
    return Collections.emptyList();
  }

  @Override
  public List<D> randomList(int size) {
    return Collections.emptyList();
  }

  @Override
  public Set<D> set() {
    return Collections.emptySet();
  }

  @Override
  public Set<D> set(int size) {
    return Collections.emptySet();
  }

  @Override
  public DataSet<D> subset(int size) {
    return this;
  }

  @Override
  @SafeVarargs
  public final DataSet<D> without(D... items) {
    return this;
  }

  @Override
  public DataSet<D> without(Collection<D> items) {
    return this;
  }

  @Override
  public DataSet<D> filter(Predicate<? super D> filter) {
    return this;
  }
  @SuppressWarnings("unchecked")
  @Override
  public <S> DataSet<S> cast(Class<S> superType) {
    return (DataSet<S>) this;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <X> DataSet<X> transform(Function<? super D, X> function, Class<X> transformedType) {
    return (DataSet<X>) this;
  }

  /*
   * @Override public DataSet<D> process(Function<? super D, Void> processor) {
   * return this; }
   */

  @Override
  public D any() {
    throw new UnsupportedOperationException("Not supported on an EmptyDataSet");
  }

  @Override
  public D any(Predicate<? super D> filter) {
    throw new UnsupportedOperationException("Not supported on an EmptyDataSet");
  }

  @Override
  public D link(Object o) {
    throw new UnsupportedOperationException("Not supported on an EmptyDataSet");
  }

  @Override
  public <L> Iterable<L> reverseLink(Class<L> type, D referred) {
    throw new UnsupportedOperationException("Not supported on an EmptyDataSet");
  }

  @Override
  public boolean isEmpty() {
    return true;
  }

  @Override
  public DataSet<D> toTransient() {
    return this;
  }

  @Override
  public DataSet<D> persist() {
    throw new UnsupportedOperationException("This dataset is not persistent:"+this.toString());
  }

  @Override
  public DataSet<D> load() {

    return this;
  }





}
