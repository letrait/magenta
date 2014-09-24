package org.magenta;

/**
 * Implementation of this interface defines the actual generation logic.
 *
 * @author ngagnon
 *
 * @param <D>
 *          the type of data being generated
 */
public interface GenerationStrategy<D, S extends DataSpecification> {

  /**
   * Generate a specific number of data.
   *
   * @param numberOfElements
   *          the number of elements to generate.
   * @param datasetMap
   *          the dataset map to use
   * @return data
   */
  public Iterable<D> generate(int numberOfElements, DataDomain<? extends S> datasetMap);

  /**
   * Generate some data, the actual number of items generated being
   * implementation-specific.
   *
   * @param datasetMap
   *          the datasetmap to use
   * @return some data
   */
  public Iterable<D> generate(DataDomain<? extends S> datasetMap);

  /**
   * Return the list of {@link DataKey}s of the data sets that will be modified when this generation strategy is used.
   * This  bidirectional relations between objects to be correctly generated.
   * @return the modified data set keys.
   */
  public Iterable<DataKey<?>> getModifiedDataSet();

}
