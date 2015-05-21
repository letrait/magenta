package org.magenta.core.data.supplier;

import java.util.Iterator;

import org.magenta.DataSupplier;

import com.google.common.base.Preconditions;

public class ResizedDataSupplierDecorator<D> extends ForwardingDataSupplier<D> implements DataSupplier<D> {

  private final int size;

  public ResizedDataSupplierDecorator(DataSupplier<D> delegate, int size) {
    super(delegate);
    Preconditions.checkArgument(size <= delegate.getMaximumSize(),
        "Illegal attempt to resize a DataSupplier [%s] greater than its maximum allowed size : %s > %s", delegate, size, delegate.getMaximumSize());
    this.size = size;
  }

  @Override
  public int getSize() {
    return size;
  }

  @Override
  public Iterator<D> iterator() {
    return new BoundedDataSupplierIterator<D>(this);
  }
}
