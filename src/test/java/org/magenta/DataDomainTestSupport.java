package org.magenta;

import java.util.Random;

import org.magenta.random.Randoms;

public class DataDomainTestSupport {

  public DataDomainManager<SimpleDataSpecification> createAnonymousDomain(){
    SimpleDataSpecification specification = SimpleDataSpecification.create();
    Randoms randomizer = Randoms.get(new Random());
    DataDomainManager<SimpleDataSpecification> domain = DataDomainManager.newRoot(getClass().getSimpleName(), specification, randomizer);
    return domain;
  }

}
