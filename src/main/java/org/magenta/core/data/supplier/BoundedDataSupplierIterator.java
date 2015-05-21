package org.magenta.core.data.supplier;

import java.util.Iterator;

import org.magenta.DataSupplier;

public class BoundedDataSupplierIterator<D> implements Iterator<D> {

  private final DataSupplier<D> supplier;
  private int index = 0;

  public BoundedDataSupplierIterator(DataSupplier<D> supplier) {
    super();
    this.supplier = supplier;
  }

  @Override
  public boolean hasNext() {
    return this.index < supplier.getSize();
  }

  @Override
  public D next() {
    D item = supplier.get(index++);
    return item;
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException("read-only iterator");

  }
}
