package org.magenta.core.data.supplier;

import java.util.Iterator;

import org.magenta.DataSupplier;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

public class FilteredDataSupplierDecorator<D> extends ForwardingDataSupplier<D> implements DataSupplier<D> {

  private Predicate<? super D> filter;

  public FilteredDataSupplierDecorator(DataSupplier<D> supplier, Predicate<? super D> filter) {
    super(supplier);
    this.filter = filter;
  }

  @Override
  public D get(int position) {
    //TODO : le Integer.MAXVALUE serait en fait le maximum du datasupplier de reference, il serait limite dans le cas d'un static
    Iterator<D> it = Iterators.filter(new ResizedDataSupplierDecorator<>(getReference(), Integer.MAX_VALUE).iterator(), filter);
    return Iterators.get(it,position);
  }

  @Override
  public Iterator<D> iterator() {
    return new BoundedDataSupplierIterator<>(this);
  }

  @Override
  public int getSize() {
    
    if(isGenerated()){
      return super.getSize();
    }else{
      return Iterators.size(Iterators.filter(getReference().iterator(), filter));
    }
    
    
  }

}
