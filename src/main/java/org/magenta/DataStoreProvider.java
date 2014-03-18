package org.magenta;

/**
 * Provides datasource.
 *
 * @author ngagnon
 *
 */
public interface DataStoreProvider {

  /**
   * Return the {@link DataStore} associated to the qualifier.
   *
   * @param key
   *          the key
   * @param <D>
   *          the type of entity
   * @return the source
   */
  public <D> DataStore<D> get(DataKey<D> key);
}
