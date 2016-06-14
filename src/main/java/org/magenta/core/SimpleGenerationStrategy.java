package org.magenta.core;


import org.magenta.DataKey;
import org.magenta.Fixture;
import org.magenta.Magenta;
import org.magenta.events.DataGenerated;

import com.google.common.base.Function;

public class SimpleGenerationStrategy<D> implements GenerationStrategy<D> {

  private final DataKey<?> key;
  private final Function<Fixture,D> generator;
  private final Function<Fixture,Integer> sizeOf;


  public SimpleGenerationStrategy(DataKey<?> key, Function<Fixture,D> generator, Function<Fixture,Integer> sizeOf) {
    this.generator = generator;
    this.sizeOf = sizeOf;
    this.key = key;
  }

  /* (non-Javadoc)
   * @see org.magenta.core.GenerationStrategy#generate(org.magenta.Fixture)
   */
  @Override
  public D generate(Fixture fixture) {

    D d =  generator.apply(fixture);

    Magenta.eventBus().post(DataGenerated.of(key, d,fixture));

    return d;
  }

  /* (non-Javadoc)
   * @see org.magenta.core.GenerationStrategy#size(org.magenta.Fixture)
   */
  @Override
  public Integer size(Fixture fixture) {
    return sizeOf.apply(fixture);
  }
}
