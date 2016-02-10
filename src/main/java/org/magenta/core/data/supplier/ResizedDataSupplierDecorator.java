package org.magenta.core.data.supplier;

import java.util.Iterator;

import org.magenta.DataSupplier;

public class ResizedDataSupplierDecorator<D> extends ForwardingDataSupplier<D> implements DataSupplier<D> {

  private final int size;

  public ResizedDataSupplierDecorator(DataSupplier<D> delegate, int size) {
    super(delegate);
    this.size = size;
  }

  @Override
  public int getSize() {
    int maxSize = getReference().isGenerated() ? Integer.MAX_VALUE: getReference().getSize();
    return size < maxSize ? size : maxSize;
  }

  @Override
  public Iterator<D> iterator() {
    return new BoundedDataSupplierIterator<D>(this);
  }
}
