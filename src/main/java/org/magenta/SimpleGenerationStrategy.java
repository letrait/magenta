package org.magenta;

/**
 * Implementers of this strategy generates item on demand and defines their own
 * preferred number of items to generate.
 *
 * @author ngagnon
 *
 * @param <D>
 * @param <S>
 */
public interface SimpleGenerationStrategy<D, S extends DataSpecification> {

  /**
   * Generate an instance of D.
   *
   * @param dataDomain
   *          the domain to use
   * @return the generated data
   */
  public D generateItem(DataDomain<? extends S> dataDomain);

  /**
   * Return the preferred number of items if none were specified while building
   * the {@link Generator} or {@link DataSet}.
   *
   * @param specification the specification from which (probably) extract the preferred number of items
   * @return the preferred number of items
   */
  public int getPreferredNumberOfItems(S specification);
}
