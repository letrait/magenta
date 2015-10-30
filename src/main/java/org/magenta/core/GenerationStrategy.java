package org.magenta.core;


import org.magenta.Fixture;

import com.google.common.base.Function;

public class GenerationStrategy<D> {

  private final Function<Fixture,D> generator;
  private final Function<Fixture,Integer> sizeOf;


  public GenerationStrategy(Function<Fixture,D> generator, Function<Fixture,Integer> sizeOf) {
    this.generator = generator;
    this.sizeOf = sizeOf;
  }

  public D generate(Fixture fixture) {

    return generator.apply(fixture);

  }

  public Integer size(Fixture fixture) {
     return sizeOf.apply(fixture);
  }
}
