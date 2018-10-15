package org.magenta.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.magenta.DataKey;
import org.magenta.DataSet;
import org.magenta.FixtureFactory;
import org.magenta.RestrictableDataSet;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.reflect.TypeToken;

public class RestrictableDataSetImpl<D>  implements RestrictableDataSet<D> {

  private FixtureFactory current;
  private final DataKey<D> key;

  public RestrictableDataSetImpl(DataKey<D> key, FixtureFactory current){
    this.current = current;
    this.key = key;
  }

  @Override
  public RestrictableDataSet<D> restrictTo(Object firstArgs, Object... remainings) {

    this.current = current.restrictTo(firstArgs, remainings);
    return this;
  }

  protected DataSet<D> resolveDataSet() {
    return this.current.doGetDatasetFunction(key).apply(current);
  }

  @Override
  public D[] array() {
    return resolveDataSet().array();
  }

  @Override
  public D[] array(int size) {
    return resolveDataSet().array(size);
  }

  @Override
  public D[] randomArray() {
    return resolveDataSet().randomArray();
  }

  @Override
  public D[] randomArray(int size) {
    return resolveDataSet().randomArray(size);
  }

  @Override
  public List<D> list() {
    return resolveDataSet().list();
  }

  @Override
  public List<D> list(int size) {
    return resolveDataSet().list(size);
  }

  @Override
  public List<D> randomList() {
    return resolveDataSet().randomList();
  }

  @Override
  public List<D> randomList(int size) {
    return resolveDataSet().randomList(size);
  }

  @Override
  public Set<D> set() {
    return resolveDataSet().set();
  }

  @Override
  public Set<D> set(int size) {
    return resolveDataSet().set(size);
  }

  @Override
  public DataSet<D> resize(int size) {
    return resolveDataSet().resize(size);
  }

  @Override
  public DataSet<D> without(D... items) {
    return resolveDataSet().without(items);
  }

  @Override
  public DataSet<D> without(Collection<D> items) {
    return resolveDataSet().without(items);
  }

  @Override
  public DataSet<D> filter(Predicate<? super D> filter) {
    return resolveDataSet().filter(filter);
  }

  @Override
  public <X> DataSet<X> transform(Function<? super D, X> function, Class<X> transformedType) {
    return resolveDataSet().transform(function, transformedType);
  }

  @Override
  public <X> DataSet<X> transform(Function<? super D, X> function, TypeToken<X> transformedType) {
    return resolveDataSet().transform(function, transformedType);
  }

  @Override
  public <S> DataSet<S> cast(Class<S> superType) {
    return resolveDataSet().cast(superType);
  }

  @Override
  public DataSet<D> freeze() {
    return resolveDataSet().freeze();
  }

  @Override
  public D any() {
    return resolveDataSet().any();
  }

  @Override
  public D any(Predicate<? super D> filter) {
    return resolveDataSet().any(filter);
  }

  @Override
  public D get(int position) {
    return resolveDataSet().get(position);
  }

  @Override
  public TypeToken<D> getType() {
    return resolveDataSet().getType();
  }

  @Override
  public int getSize() {
    return resolveDataSet().getSize();
  }

  @Override
  public D first() {
    return resolveDataSet().first();
  }

  @Override
  public boolean isEmpty() {
    return resolveDataSet().isEmpty();
  }

  @Override
  public boolean isGenerated() {
    return resolveDataSet().isGenerated();
  }

  @Override
  public boolean isConstant() {
    return resolveDataSet().isConstant();
  }

  @Override
  public Iterator<D> iterator() {
    return resolveDataSet().iterator();
  }
}
