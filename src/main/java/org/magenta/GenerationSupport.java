package org.magenta;

import org.magenta.annotations.InjectDataSpecification;
import org.magenta.annotations.InjectRandomBuilder;
import org.magenta.random.RandomBuilder;

import com.google.common.base.Supplier;

public class GenerationSupport<S extends DataSpecification> {

  @InjectRandomBuilder
  private RandomBuilder randomBuilder;

  @InjectDataSpecification
  private Supplier<S> spec;

  public S getDataSpecification(){
    return spec.get();
  }

  public RandomBuilder getRandomBuilder(){
    return randomBuilder;
  }

}
