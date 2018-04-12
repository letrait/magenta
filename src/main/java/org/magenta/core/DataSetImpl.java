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
import org.magenta.Magenta;
import org.magenta.UnboundedDataSetException;
import org.magenta.core.data.supplier.BoundedDataSupplierIterator;
import org.magenta.core.data.supplier.FilteredDataSupplierDecorator;
import org.magenta.core.data.supplier.ResizedDataSupplierDecorator;
import org.magenta.core.data.supplier.StaticDataSupplier;
import org.magenta.core.data.supplier.TransformedDataSupplierDecorator;
import org.magenta.events.DataReturned;
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
  private boolean postEventEnabled;

  /**
   * Construct a new dataset using the provided supplier as source.
   * @param datasetSupplier The data supplier
   * @param postEventEnabled if event must be posted when data are read.
   */
  public DataSetImpl(DataSupplier<D> datasetSupplier, boolean postEventEnabled) {
    this.supplier = datasetSupplier;
    this.postEventEnabled = postEventEnabled;
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
  public D get(int index) {
    D data = this.supplier.get(index);

    if(postEventEnabled){
      Magenta.eventBus().post(DataReturned.of(data, index));
    }

    return data;
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
      checkBound();
      return FluentRandom.iterable(this).any();
    }
  }



  public boolean isUnbounded() {
    return getSize() == Integer.MAX_VALUE;
  }

  @Override
  public D any(Predicate<? super D> filter) {
    checkBound();
    return FluentRandom.iterable(Iterables.filter(this, filter)).any();
  }

  @Override
  public DataSet<D> resize(int size) {
    return new DataSetImpl<D>(new ResizedDataSupplierDecorator<D>(this.supplier,size), false);
  }

  @Override
  public DataSet<D> filter(Predicate<? super D> filter) {
    checkBound();
    return new DataSetImpl<D>(new FilteredDataSupplierDecorator<D>(this.supplier,filter), false);
  }

  @Override
  public <X> DataSet<X> transform(Function<? super D, X> function, Class<X> newType) {
    return transform(function, TypeToken.of(newType));
  }

  @Override
  public <X> DataSet<X> transform(Function<? super D, X> function, TypeToken<X> transformedType) {
    return new DataSetImpl<X>(new TransformedDataSupplierDecorator<D,X>(this.supplier,function, transformedType), false);
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
  public DataSet<D> freeze() {
    return new DataSetImpl<>(new StaticDataSupplier<>(list(), getType()), false);
  }

  @Override
  public D[] array() {
    checkBound();
    return Iterables.toArray(this, (Class<D>)this.supplier.getType().getRawType());
  }

  @Override
  public D[] array(int size) {
    return resize(size).array();
  }

  @Override
  public D[] randomArray() {
    checkBound();
    return Iterables.toArray(randomList(), (Class<D>)this.supplier.getType().getRawType());
  }

  @Override
  public D[] randomArray(int size) {
    return resize(size).randomArray();
  }

  @Override
  public List<D> list() {
    checkBound();
    return Lists.newArrayList(this.iterator());
  }

  @Override
  public List<D> list(int size) {
    return resize(size).list();
  }

  @Override
  public List<D> randomList() {
    checkBound();
    return FluentRandom.iterable(this).shuffle().list();
  }

  @Override
  public List<D> randomList(int size) {
    return resize(size).randomList();
  }

  @Override
  public Set<D> set() {
    checkBound();
    return Sets.newLinkedHashSet(this);
  }

  @Override
  public Set<D> set(int size) {
    return resize(size).set();
  }

  protected void checkBound(){
    if(isUnbounded()) {
      throw new UnboundedDataSetException(String.format("The dataset %s is unbounded so it is not possible to select using any", this.getType()));
    }
  }


  @Override
  public int hashCode() {
    return !this.isGenerated() ? this.getSize() : super.hashCode();
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
