package org.magenta.core.data.supplier;


import org.magenta.Fixture;
import org.magenta.core.FixtureContext;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

public class ContextualizedSupplierFunctionAdapter<D> implements Function<Fixture,D> {

  private final Supplier<D> delegate;
  private final FixtureContext context;

  public ContextualizedSupplierFunctionAdapter(Supplier<D> delegate, FixtureContext context) {
    super();
    this.delegate = delegate;
    this.context = context;
  }

  @Override
  public D apply(Fixture fixture) {
    boolean managed = false;
    try {
      if (!context.isSet()) {
        managed = true;
        context.set(fixture);
      }
      D data = delegate.get();
      return data;
    } finally {
      if (managed) {
        context.clear();
      }
    }
  }

  @Override
  public String toString(){

    return "contextualized "+delegate.toString();
  }

}
