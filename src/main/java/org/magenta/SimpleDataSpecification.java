package org.magenta;


public class SimpleDataSpecification extends DataSpecificationImpl<SimpleDataSpecification>{

  protected SimpleDataSpecification() {
    super(SimpleDataSpecification.class);
  }

  public static SimpleDataSpecification create(){
    return new SimpleDataSpecification();
  }

}