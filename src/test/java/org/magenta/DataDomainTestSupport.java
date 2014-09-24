package org.magenta;

import java.util.Random;

import org.magenta.random.RandomBuilder;

public class DataDomainTestSupport {

  public DataDomainManager<SimpleDataSpecification> createAnonymousDomain(){
    SimpleDataSpecification specification = SimpleDataSpecification.create();
    RandomBuilder randomizer = RandomBuilder.PROVIDER.get(new Random());
    DataDomainManager<SimpleDataSpecification> domain = DataDomainManager.newRoot(getClass().getSimpleName(), specification, randomizer);
    return domain;
  }

}
