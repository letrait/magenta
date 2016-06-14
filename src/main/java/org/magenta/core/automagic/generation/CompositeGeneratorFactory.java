package org.magenta.core.automagic.generation;

import java.util.Collection;

import org.magenta.DataKey;
import org.magenta.FixtureFactory;
import org.magenta.core.GenerationStrategy;

import com.google.common.base.Optional;

public class CompositeGeneratorFactory implements DynamicGeneratorFactory {

  private final Collection<DynamicGeneratorFactory> generatorProviders;

  public CompositeGeneratorFactory( Collection<DynamicGeneratorFactory> generatorProviders) {
    this.generatorProviders = generatorProviders;

  }

  @Override
  public <D> Optional<? extends GenerationStrategy<D>> buildGeneratorOf(DataKey<D> type, FixtureFactory fixture, DynamicGeneratorFactory dynamicGeneratorFactory) {

    for(DynamicGeneratorFactory p:generatorProviders){

      Optional<? extends GenerationStrategy<D>> s = p.buildGeneratorOf(type,fixture,dynamicGeneratorFactory);
      if(s.isPresent()){
        return s;
      }
    }

    return Optional.absent();

  }
}
