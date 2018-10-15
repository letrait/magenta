package org.magenta;

import java.util.List;
import java.util.Set;


public interface Fixture {

  public <D> D any(Class<D> type);

  public <D> D any(Class<D> type, Object firstRestriction, Object...rest);

  <D> D first(Class<D> type);

  <D> D first(Class<D> type, Object firstRestriction, Object... rest);

  <D> List<D> list(Class<D> type);

  <D> List<D> list(Class<D> type, Integer size, Object firstRestriction, Object...rest) ;

  <D> List<D> list(Class<D> type, Object firstRestriction, Object...rest) ;

  public <D> RestrictableDataSet<D> dataset(DataKey<D> key);

  public <D> RestrictableDataSet<D> dataset(Class<D> type);

  public Set<DataKey<?>> keys();

  public Fixture restrictTo(Object first, Object...theRest);

  public Fixture init(Class<?> clazz);

  public Fixture init(DataKey<?> key);

  public Fixture init(Class<?> clazz, int resize);

  public Fixture init(DataKey<?> key, int resize);

}
