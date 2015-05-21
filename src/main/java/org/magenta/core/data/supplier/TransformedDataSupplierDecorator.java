package org.magenta.core.data.supplier;

import java.util.Iterator;

import org.magenta.DataSupplier;

import com.google.common.base.Function;
import com.google.common.reflect.TypeToken;

public class TransformedDataSupplierDecorator<S,X> implements DataSupplier<X> {

  public TransformedDataSupplierDecorator(DataSupplier<S> supplier, Function<? super S, X> function) {
    // TODO Auto-generated constructor stub
  }

  @Override
  public Iterator<X> iterator() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public X get(int position) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public TypeToken<X> getType() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int getSize() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int getMaximumSize() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public boolean isEmpty() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isGenerated() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isConstant() {
    // TODO Auto-generated method stub
    return false;
  }


}
