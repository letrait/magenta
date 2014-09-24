package org.magenta;

import java.util.Set;

import org.magenta.random.RandomBuilder;

/**
 * A <code>DataDomain</code> groups together different type of related {@link DataSet} and
 * {@link Generator} so they can be retrieved in a convenient way.
 *
 * @author ngagnon
 */
public interface DataDomain<S extends DataSpecification> {

  /**
   * @return the display name of this data domain.
   */
  public String getName();

  /**
   * Return the particular {@linkplain DataSpecification} implementation of this
   * domain.
   *
   * @return the specification
   */
  public S getSpecification();

  /**
   * Return the parent domain of this domain.
   *
   * @return the parent domain or null if this domain has no parent.
   */
  public DataDomain<S> getParent();

  /**
   * Return the {@linkplain RandomBuilder} associated to this domain.
   *
   * @return the randomizer
   */
  public RandomBuilder getRandomizer();

  /**
   * Return the {@linkplain DataSet} associated to the default key of
   * <code>clazz</code>.
   *
   * @param clazz
   *          the dataset data type
   * @param <D>
   *          the type of data managed by the data set.
   * @return a DataSet
   */
  public abstract <D> DataSet<D> dataset(Class<D> clazz);


  public Integer numberOfElementsFor(DataKey<?> key);

  /**
   * Return the {@linkplain DataSet} associated to the specified key.
   *
   * @param key
   *          the dataset data type
   * @param <D>
   *          the type of data managed by the data set.
   * @return a DataSet
   */
  public abstract <D> DataSet<D> dataset(DataKey<D> key);

  /**
   * Return the set of keys for which there is a {@link DataSet} in this domain.
   *
   * @return a set of keys
   */
  public Set<DataKey<?>> datasetKeys();

  /**
   * Return the collection of {@link DataSet} associated to this domain.
   *
   * @return an iterable of dataset.
   */
  public Iterable<DataSet<?>> datasets();

  /**
   * Return the generator identified by the passed in <code>class</code>.
   *
   * @param clazz the generator class identifier
   * @param <D> the generated data type
   * @return a generator
   */
  public abstract <D> Generator<D> generator(Class<D> clazz);

  /**
   * Return the generator identified by the passed in <code>key</code>.
   * @param key the generator key identifier
   * @param <D> the generated data type
   * @return a generator
   */
  public abstract <D> Generator<D> generator(DataKey<D> key);

  /**
   * Return the {@link GenerationStrategy} identified by the passed in key.
   *
   * @param key
   *          the key
   * @param <D>
   *          the generated data type
   *
   * @return the {@link GenerationStrategy}
   */
  public <D> GenerationStrategy<D, S> strategy(DataKey<D> key);

  /**
   * Return the {@link GenerationStrategy} identified by the passed in <code>clazz</code>.
   *
   * @param clazz
   *          the key
   * @param <D>
   *          the generated data type
   *
   * @return the {@link GenerationStrategy}
   */
  public <D> GenerationStrategy<D, S> strategy(Class<D> clazz);

  /**
   * Return the set of keys for which there is a {@link GenerationStrategy} in
   * this domain.
   *
   * @return a set of {@link DataKey}
   */
  public Set<DataKey<?>> strategyKeys();

  /**
   * Return the collection of {@link GenerationStrategy} associated to this
   * domain.
   *
   * @return an iterable of dataset.
   */
  public Iterable<GenerationStrategy<?, ? extends DataSpecification>> strategies();

}