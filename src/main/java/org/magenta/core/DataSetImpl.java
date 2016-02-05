package org.magenta.core;

import static com.google.common.base.Predicates.in;
import static com.google.common.base.Predicates.not;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.magenta.DataSet;
import org.magenta.DataSupplier;
import org.magenta.core.data.supplier.BoundedDataSupplierIterator;
import org.magenta.core.data.supplier.FilteredDataSupplierDecorator;
import org.magenta.core.data.supplier.ResizedDataSupplierDecorator;
import org.magenta.core.data.supplier.TransformedDataSupplierDecorator;
import org.magenta.random.FluentRandom;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;

/**
 * Implementation of a {@link DataSet} based on a guava {@link Supplier} of
 * Iterable.
 *
 * @author ngagnon
 *
 * @param <D>
 *          the data type
 */
public class DataSetImpl<D> implements DataSet<D> {

  protected final DataSupplier<D> supplier;

  /**
   * Construct a new dataset using the provided supplier as source.
   *
   * @param type
   *          the type of data
   * @param random
   *          the java random to use for shuffling and by the "any()" method
   */
  public DataSetImpl(DataSupplier<D> datasetSupplier) {
    this.supplier = datasetSupplier;
  }

  @Override
  public TypeToken<D> getType() {
    return this.supplier.getType();
  }

  @Override
  public boolean isConstant() {
    return this.supplier.isConstant();
  }

  @Override
  public boolean isGenerated() {
    return this.supplier.isGenerated();
  }

  @Override
  public D get(int position) {
    return this.supplier.get(position);
  }

  @Override
  public int getSize() {
    return this.supplier.getSize();
  }

  @Override
  public boolean isEmpty() {
    return supplier.isEmpty();
  }


  @Override
  public Iterator<D> iterator() {
     return new BoundedDataSupplierIterator<D>(this);
  }

  @Override
  public D first() {
   return get(0);
  }

  @Override
  public D any() {
    if(this.isGenerated() && !this.isConstant()){
     return get(0);
    }else{
      return FluentRandom.iterable(this).any();
    }
  }

  @Override
  public D any(Predicate<? super D> filter) {
    return FluentRandom.iterable(Iterables.filter(this, filter)).any();
  }

  @Override
  public DataSet<D> resize(int size) {
    return new DataSetImpl<D>(new ResizedDataSupplierDecorator<D>(this.supplier,size));
  }

  @Override
  public DataSet<D> filter(Predicate<? super D> filter) {
    return new DataSetImpl<D>(new FilteredDataSupplierDecorator<D>(this.supplier,filter));
  }

  @Override
  public <X> DataSet<X> transform(Function<? super D, X> function, Class<X> newType) {
    return new DataSetImpl<X>(new TransformedDataSupplierDecorator<D,X>(this.supplier,function, TypeToken.of(newType)));
  }

  @Override
  public <X> DataSet<X> cast(final Class<X> type) {

    return transform( new Function<D,X>(){

      @Override
      public X apply(D input) {
        return type.cast(input);
      }

    }, type);
  }


  @Override
  @SafeVarargs
  public final DataSet<D> without(D... items) {
    return filter(not(in(Arrays.asList(items))));
  }

  @Override
  public DataSet<D> without(Collection<D> items) {
    return filter(not(in(items)));
  }

  @Override
  public D[] array() {
    return Iterables.toArray(this, (Class<D>)this.supplier.getType().getRawType());
  }

  @Override
  public D[] array(int size) {
    return resize(size).array();
  }

  @Override
  public D[] randomArray() {
    return Iterables.toArray(randomList(), (Class<D>)this.supplier.getType().getRawType());
  }

  @Override
  public D[] randomArray(int size) {
    return resize(size).randomArray();
  }

  @Override
  public List<D> list() {
    return Lists.newArrayList(this);
  }

  @Override
  public List<D> list(int size) {
    return resize(size).list();
  }

  @Override
  public List<D> randomList() {
    return FluentRandom.iterable(this).shuffle().list();
  }

  @Override
  public List<D> randomList(int size) {
    return resize(size).randomList();
  }

  @Override
  public Set<D> set() {
    return Sets.newLinkedHashSet(this);
  }

  @Override
  public Set<D> set(int size) {
    return resize(size).set();
  }

  public DataSupplier<D> getSupplier() {
    return this.supplier;
  }


  @Override
  public int hashCode() {
    return !this.isGenerated()? this.hashCode() : super.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }

    if (obj instanceof DataSet) {
      @SuppressWarnings("unchecked")
      DataSet<D> other = (DataSet<D>) obj;

      return !this.isGenerated() && !other.isGenerated() && Iterables.elementsEqual(this, other);
    }
    return false;
  }


}
