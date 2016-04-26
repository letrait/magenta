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
    return this.dataset.get(coordinator.getIndexFor(this));

  }

  @Override
  public int size() {
    return this.dataset.getSize();
  }

  public boolean hasUnicityConstraint() {
    return this.unique;
  }

}
