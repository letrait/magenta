package org.magenta.core.sequence;

import org.magenta.DataSet;
import org.magenta.Sequence;

public class CoordinatedSequence<D> implements Sequence<D> {

  private final DataSet<D> dataset;
  private final SequenceCoordinator coordinator;
  private final boolean unique;

  public CoordinatedSequence(DataSet<D> dataset, boolean unique, SequenceCoordinator coordinator) {
    this.dataset = dataset;
    this.unique = unique;
    this.coordinator = coordinator;
    this.coordinator.coordinate(this);
  }

  @Override
  public D next() {

    int index = coordinator.getIndexFor(this);

    D data = this.dataset.get(index);

    return data;

  }

  @Override
  public int size() {
    return this.dataset.getSize();
  }

  public boolean hasUnicityConstraint() {
    return this.unique;
  }

}
