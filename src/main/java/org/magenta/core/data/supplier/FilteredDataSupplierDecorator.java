package org.magenta.core.data.supplier;

import java.util.Iterator;

import org.magenta.DataSupplier;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public class FilteredDataSupplierDecorator<D> extends ForwardingDataSupplier<D> implements DataSupplier<D> {

  private Predicate<? super D> filter;

  public FilteredDataSupplierDecorator(DataSupplier<D> supplier, Predicate<? super D> filter) {
    super(supplier);
    this.filter = filter;
  }

  @Override
  public D get(int position) {
    return Iterables.get(Iterables.filter(this, filter), position);
  }

  @Override
  public Iterator<D> iterator() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int getSize() {
    return Iterables.size(this);
  }

}
