package org.magenta.core.data.supplier;

import java.util.Iterator;

import org.magenta.DataSupplier;

import com.google.common.base.Supplier;
import com.google.common.reflect.TypeToken;

public class GeneratorDataSupplier<D> implements DataSupplier<D> {

  private final Supplier<D> generator;
  
  private final Supplier<Integer> sizeOf;

  private final TypeToken<D> type;

  public GeneratorDataSupplier( TypeToken<D> type,Supplier<D> generator, Supplier<Integer> sizeOf) {
    this.generator = generator;
    this.type = type;
    this.sizeOf = sizeOf;
  }

  @Override
  public Iterator<D> iterator() {
   return new BoundedDataSupplierIterator<D>(this);
  }

  @Override
  public D get(int position) {
    return generator.get();
  }

  @Override
  public TypeToken<D> getType() {
    return type;
  }

  @Override
  public int getSize() {
    return this.sizeOf.get() ;
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public boolean isGenerated() {
   return true;
  }

  @Override
  public boolean isConstant() {
    return false;
  }

}
