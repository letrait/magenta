package org.magenta;

/**
 * A dataset which know its own {@link DataKey}.
 *
 * @author ngagnon
 *
 * @param <D>
 */
public interface QualifiedDataSet<D> extends DataSet<D> {

  /**
   * The {@link DataKey} associated to this QualifiedDataSet.
   *
   * @return the qualifier
   */
  public DataKey<D> getKey();
}
