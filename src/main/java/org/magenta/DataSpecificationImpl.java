package org.magenta;

/**
 * Base {@link DataSpecification} implementation.
 *
 * @author ngagnon
 *
 */
public class DataSpecificationImpl<C> implements DataSpecification {

  private static final int DEFAULT_NUMBER_OF_ITEMS = 10;
  private int defaultNumberOfItems;
  private C myself;

  protected DataSpecificationImpl(Class<?> selfType){
    this.myself = (C)selfType.cast(this);
    this.defaultNumberOfItems = DEFAULT_NUMBER_OF_ITEMS;
  }



  /**
   * Return the default number of items to generate.
   *
   * @return the default number of items to generate
   */
  @Override
  public int getDefaultNumberOfItems(){
    return this.defaultNumberOfItems;
  }


  public C defaultNumberOfItems(int defaultNumberOfItems){
    this.defaultNumberOfItems = defaultNumberOfItems;
    return myself();

  }

  public C myself(){
    return myself;
  }

}
