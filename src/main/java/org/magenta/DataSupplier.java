package org.magenta;

import com.google.common.reflect.TypeToken;

public interface DataSupplier<D> extends Iterable<D> {

  D get(int position);

  TypeToken<D> getType();

  int getSize();


  boolean isEmpty();

  boolean isGenerated();

  boolean isConstant();

}
