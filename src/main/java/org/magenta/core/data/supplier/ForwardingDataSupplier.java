package org.magenta.core.data.supplier;

import java.util.Iterator;

import org.magenta.DataSupplier;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.reflect.TypeToken;

public class ForwardingDataSupplier<D> implements DataSupplier<D> {

  private final Supplier<? extends DataSupplier<D>> reference;

  public ForwardingDataSupplier(DataSupplier<D> directReference) {
    super();
    this.reference = Suppliers.ofInstance(directReference);
  }

  public ForwardingDataSupplier(Supplier<? extends DataSupplier<D>> reference) {
    super();
    this.reference = reference;
  }

  public DataSupplier<D> getReference(){
    return this.reference.get();
  }

  @Override
  public Iterator<D> iterator() {
    return getReference().iterator();
  }

  @Override
  public D get(int position) {
    return getReference().get(position);
  }

  @Override
  public boolean isGenerated() {
    return getReference().isGenerated();
  }

  @Override
  public boolean isConstant() {
    return getReference().isConstant();
  }

  @Override
  public TypeToken<D> getType() {
    return getReference().getType();
  }

  @Override
  public boolean isEmpty() {
    return getReference().isEmpty();
  }

  @Override
  public int getSize() {
    return getReference().getSize();
  }

  @Override
  public int getMaximumSize() {
   return getReference().getMaximumSize();
  }


}
