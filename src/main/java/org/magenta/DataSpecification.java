package org.magenta;


/**
 * Implementation of this base interface are used to parameterize {@link Fixture} and are used by {@link GenerationStrategy} as hints on how
 * to generate data.  This interface should be extended with your own specific parameters.
 *
 * @author ngagnon
 *
 */
public interface DataSpecification {


  /**
   * Return the default number of items to generate.
   *
   * @return the default number of items to generate
   */
  public int getDefaultNumberOfItems();


}
