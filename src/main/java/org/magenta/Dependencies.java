package org.magenta;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.magenta.core.FixtureContext;
import org.magenta.core.GenerationStrategyFactory;
import org.magenta.core.Injector;
import org.magenta.core.automagic.generation.CompositeGeneratorFactory;
import org.magenta.core.automagic.generation.DataKeyDeterminedFromFieldTypeMappingFunction;
import org.magenta.core.automagic.generation.DynamicGeneratorFactory;
import org.magenta.core.automagic.generation.provider.ConditionalGeneratorFactory;
import org.magenta.core.automagic.generation.provider.ObjectGeneratorFactory;
import org.magenta.core.automagic.generation.provider.PrimitiveDynamicGeneratorFactoryProvider;
import org.magenta.core.context.ThreadLocalFixtureContext;
import org.magenta.core.injector.FieldInjectionChainProcessor;
import org.magenta.core.injector.FieldInjectionHandler;
import org.magenta.core.injector.FieldsExtractor;
import org.magenta.core.injector.extractors.HiearchicalFieldsExtractor;
import org.magenta.core.injector.handlers.DataSetFieldHandler;
import org.magenta.core.injector.handlers.SequenceFieldHandler;
import org.magenta.random.FluentRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class Dependencies {

  private static Logger log = LoggerFactory.getLogger(Dependencies.class);

  public FixtureFactory fixtureFactory() {

    FluentRandom.incrementSeed();

    FixtureContext fixtureContext = fixtureContext();
    DynamicGeneratorFactory generatorFactory = generatorFactory();
    return new FixtureFactory(null, generationStrategyFactory(fixtureContext), generatorFactory, fixtureContext);
  }

  public DynamicGeneratorFactory generatorFactory() {
    List<DynamicGeneratorFactory> factories = Lists.newArrayList();

    factories.addAll(PrimitiveDynamicGeneratorFactoryProvider.get());
    factories.add(new ConditionalGeneratorFactory(type -> type.isSubtypeOf(Date.class), () -> FluentRandom.dates().any()));
    factories.add(new ObjectGeneratorFactory( fieldExtractor(), new DataKeyDeterminedFromFieldTypeMappingFunction()));

    return new CompositeGeneratorFactory(factories);
  }

  public GenerationStrategyFactory generationStrategyFactory(FixtureContext fixtureContext) {
    return new GenerationStrategyFactory(fixtureContext, injector());
  }

  public Injector injector(){
    return new FieldInjectionChainProcessor(fieldInjectionHandlers());
  }

  public List<FieldInjectionHandler> fieldInjectionHandlers(){

    List<FieldInjectionHandler> handlers = Lists.newArrayList();

    handlers.add(new DataSetFieldHandler(fieldExtractor()));
    handlers.add(new SequenceFieldHandler(fieldExtractor()));

    return handlers;

  }



  public FieldsExtractor fieldExtractor() {
    return HiearchicalFieldsExtractor.SINGLETON;
  }

  public FixtureContext fixtureContext() {
    return new ThreadLocalFixtureContext();
  }


}
