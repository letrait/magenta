package org.magenta;

/**
 * Implementers of this interface generates collection of the generic types
 * where the number of items to generate is not configurable or applicable and
 * is implementation specific.
 *
 * @author ngagnon
 *
 * @param <D> the type of data being generated
 * @param <S> the data specification required by this implementation
 */
public interface ImplicitGenerationStrategy<D, S extends DataSpecification> {
  /**
   * Generate some data, the actual number of items generated is
   * implementation-specific.
   *
   * @param datasetMap
   *          the datasetmap to use
   * @return some data
   */
  public Iterable<D> generate(DataDomain<? extends S> datasetMap);
}
