package org.magenta.core.data.supplier;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;
import java.util.List;

import org.magenta.DataSupplier;

import com.google.common.reflect.TypeToken;

public class StaticDataSupplier<D> implements DataSupplier<D>{

  private List<D> data;
  private TypeToken<D> type;

  public StaticDataSupplier(List<D> data, TypeToken<D> type){
    this.data = checkNotNull(data);
    this.type = type;
  }

  @Override
  public Iterator<D> iterator() {
    return data.iterator();
  }

  @Override
  public D get(int position) {
    return data.get(position);
  }

  @Override
  public boolean isGenerated() {
    return false;
  }

  @Override
  public boolean isConstant() {
    return true;
  }

  @Override
  public TypeToken<D> getType() {
    return type;
  }

  @Override
  public boolean isEmpty() {
    return data.isEmpty();
  }

  @Override
  public int getSize() {
    return data.size();
  }
}
