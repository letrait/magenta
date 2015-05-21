package org.magenta.core.sequence;

import org.magenta.DataSet;
import org.magenta.Sequence;

public class CoordinatedSequence<D> implements Sequence<D> {

  private DataSet<D> dataset;
  private SequenceCoordinator coordinator;

  public CoordinatedSequence(DataSet<D> dataset, SequenceCoordinator coordinator) {
    this.dataset = dataset;
    this.coordinator = coordinator;
    this.coordinator.coordinate(this);
  }

  @Override
  public D get() {
    return this.dataset.get(coordinator.getIndexFor(this));

  }

  public int size() {
    return this.dataset.getSize();
  }

}
