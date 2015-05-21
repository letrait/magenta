package org.magenta.core.data.supplier;

import java.util.Iterator;

import org.magenta.DataSupplier;
import org.magenta.Sequence;

import com.google.common.reflect.TypeToken;

public class GeneratorDataSupplier<D> implements DataSupplier<D> {

  public static final int NO_SPECIFIED_DEFAULT_SIZE = -1;

  private final Sequence<D> generator;

  private final TypeToken<D> type;

  private final int defaultSize;
  private final int maximumAllowedSize;

  public GeneratorDataSupplier(Sequence<D> generator, TypeToken<D> type, int defaultSize, int maxSize) {
    this.generator = generator;
    this.type = type;
    this.defaultSize = defaultSize;
    this.maximumAllowedSize = maxSize;
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
    return this.defaultSize == NO_SPECIFIED_DEFAULT_SIZE? this.generator.size() : this.defaultSize;
  }

  @Override
  public int getMaximumSize() {
    return maximumAllowedSize;
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
