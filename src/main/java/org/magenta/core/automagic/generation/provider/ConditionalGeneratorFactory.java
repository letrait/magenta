package org.magenta.core.automagic.generation.provider;

import org.magenta.DataKey;
import org.magenta.Fixture;
import org.magenta.FixtureFactory;
import org.magenta.core.GenerationStrategy;
import org.magenta.core.SimpleGenerationStrategy;
import org.magenta.core.automagic.generation.DynamicGeneratorFactory;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.reflect.TypeToken;

public class ConditionalGeneratorFactory implements DynamicGeneratorFactory{


  private final Predicate<TypeToken<?>> spec;
  private final Function<? super Fixture,?> generator;


  // the injector could be used to inject into the generator
  // the supplier could be a function that take as input the field or the class of the candidate
  public <D> ConditionalGeneratorFactory(Predicate<TypeToken<D>> spec, Supplier<D> generator) {
    super();
    this.spec = (Predicate)spec;
    this.generator = Functions.forSupplier(generator);
  }

  @Override
  public <D> Optional<GenerationStrategy<D>> buildGeneratorOf(DataKey<D> key, FixtureFactory fixture,
      DynamicGeneratorFactory dynamicGeneratorFactory) {

    Function<Fixture,D> g = (Function)generator;

    return spec.apply(key.getType()) ? Optional.of(new SimpleGenerationStrategy<D>(key, g,f -> 1)) : Optional.absent();
  }

}
