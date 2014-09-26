package org.magenta;

import org.magenta.annotations.InjectDataSpecification;
import org.magenta.annotations.InjectFixture;
import org.magenta.annotations.InjectFluentRandom;
import org.magenta.random.FluentRandom;

public class GenerationSupport<S extends DataSpecification> {

  @InjectFluentRandom
  private FluentRandom randomBuilder;

  @InjectDataSpecification
  private S spec;

  @InjectFixture
  private Fixture<S> fixture;

  public S getDataSpecification(){
    return spec;
  }

  public FluentRandom getRandomBuilder(){
    return randomBuilder;
  }

  public Fixture<S> getFixture(){
    return fixture;
  }

}
