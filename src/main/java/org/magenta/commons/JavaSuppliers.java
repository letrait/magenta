package org.magenta.commons;

import java.util.function.Supplier;

import com.google.common.base.Suppliers;

public class JavaSuppliers {

  public static <D> Supplier<D> memoize(Supplier<D> delegate) {

    com.google.common.base.Supplier<D> g = Suppliers.memoize(()->delegate.get());

    return () -> g.get();

  }


}
