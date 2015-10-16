package org.magenta;

import java.util.Set;


public interface Fixture {

  public <D> DataSet<D> dataset(DataKey<D> key);

  public <D> DataSet<D> dataset(Class<D> type);

  public Set<DataKey<?>> keys();

  public Fixture restrictTo(Object first, Object...theRest);
}
