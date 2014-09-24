package org.magenta;

/**
 * Implementers of this strategy generates item on demand and defines their own
 * preferred number of items to generate.
 *
 * @author ngagnon
 *
 * @param <D>
 * @param <S>
 * @deprecated prefer usage of Supplier with annotation.
 */
@Deprecated
public interface SimpleGenerationStrategy<D, S extends DataSpecification> {

  /**
   * Generate an instance of D.
   *
   * @param dataDomain
   *          the domain to use
   * @return the generated data
   */
  public D generateItem(DataDomain<? extends S> dataDomain);

}
