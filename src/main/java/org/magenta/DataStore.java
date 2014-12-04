package org.magenta;

/**
 * A datasource is used to retrieve and persist entities.
 *
 * @author ngagnon
 *
 * @param <D>
 */
public interface DataStore<D> {

  /**
   * Return the persisted instance of the specified entity.
   *
   * @param  detachedEntity the entity
   * @return the persisted entity or null otherwise.
   */
  public D retrieve(D detachedEntity);

  /**
   * Make the specified entiy persistent.
   *
   * @param transientEntity the entity to persist
   * @return a persisted entity
   */
  public D persist(D transientEntity);

  /**
   * Return a
   */
  public static DataStore<Object> IDENTITY = new DataStore<Object>(){

    @Override
    public Object retrieve(Object detachedEntity) {
     return detachedEntity;
    }

    @Override
    public Object persist(Object transientEntity) {
      return transientEntity;
    }

  };

}