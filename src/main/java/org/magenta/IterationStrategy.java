package org.magenta;

public interface IterationStrategy {

  public <D> D next(DataSet<D> dataset);
}
