package org.magenta;

import java.util.Random;

import org.magenta.random.FluentRandom;

public class FixtureFactoryTestSupport {

  public FixtureFactory<SimpleDataSpecification> createAnonymousFixtureFactory(){
    SimpleDataSpecification specification = SimpleDataSpecification.create();
    FluentRandom randomizer = FluentRandom.get(new Random());
    FixtureFactory<SimpleDataSpecification> domain = FixtureFactory.newRoot(getClass().getSimpleName(), specification, randomizer);
    return domain;
  }



}
