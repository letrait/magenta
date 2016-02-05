package org.magenta.core.data.supplier;

import java.util.Iterator;

import org.magenta.DataSupplier;

import com.google.common.base.Function;
import com.google.common.reflect.TypeToken;

public class TransformedDataSupplierDecorator<S,X> implements DataSupplier<X> {

  private DataSupplier<S> delegate;
  private Function<? super S, X> transform;
  private TypeToken<X> type;
  
  public TransformedDataSupplierDecorator(DataSupplier<S> delegate, Function<? super S, X> transform, TypeToken<X> type) {
  this.delegate =delegate;
  this.transform=transform;
  this.type =type;
  }

  @Override
  public Iterator<X> iterator() {
    return new BoundedDataSupplierIterator<>(this);
  }

  @Override
  public X get(int position) {
    return transform.apply(delegate.get(position));
  }

  @Override
  public TypeToken<X> getType() {
    return type;
  }

  @Override
  public int getSize() {
    return delegate.getSize();
  }

  @Override
  public boolean isEmpty() {
    return delegate.isEmpty();
  }

  @Override
  public boolean isGenerated() {
    return delegate.isGenerated();
  }

  @Override
  public boolean isConstant() {
   return delegate.isConstant();
  }

  



}
