package org.magenta.core.sequence;

import java.util.Iterator;

import org.magenta.DataSet;
import org.magenta.Sequence;

public class OrderedSequence<D> implements Sequence<D> {

  private int index;
  private DataSet<D> dataset;

  public static <D> OrderedSequence<D> from(DataSet<D> dataset) {
    return new OrderedSequence<D>(dataset);
  }

  private OrderedSequence(DataSet<D> dataset) {
    this.dataset = dataset;
    this.index = 0;
  }

  @Override
  public D get() {


    if (index >= dataset.getSize()) {
      index = 0;
    }

    return dataset.get(index++);
  }

  @Override
  public int size(){
    return dataset.getSize();
  }
  

}
