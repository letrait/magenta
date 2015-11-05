package org.magenta.core;


import org.magenta.Fixture;

import com.google.common.base.Function;

public class SimpleGenerationStrategy<D> implements GenerationStrategy<D> {

  private final Function<Fixture,D> generator;
  private final Function<Fixture,Integer> sizeOf;


  public SimpleGenerationStrategy(Function<Fixture,D> generator, Function<Fixture,Integer> sizeOf) {
    this.generator = generator;
    this.sizeOf = sizeOf;
  }

  /* (non-Javadoc)
   * @see org.magenta.core.GenerationStrategy#generate(org.magenta.Fixture)
   */
  @Override
  public D generate(Fixture fixture) {

    return generator.apply(fixture);

  }

  /* (non-Javadoc)
   * @see org.magenta.core.GenerationStrategy#size(org.magenta.Fixture)
   */
  @Override
  public Integer size(Fixture fixture) {
     return sizeOf.apply(fixture);
  }
}
