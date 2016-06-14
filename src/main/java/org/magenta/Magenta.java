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

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;

public class Magenta {

  private static Logger log = LoggerFactory.getLogger(Magenta.class);

  public static AtomicLong DEFAULT_SEED_FOR_RANDOM = new AtomicLong(0L);

  private static final Supplier<Dependencies> dependencies = Suppliers.memoize(new Supplier<Dependencies>() {

    @Override
    public Dependencies get() {
      return new Dependencies();
    }

  });

  private static final Supplier<EventBus> eventBus = Suppliers.memoize(()->new EventBus());

  public static FixtureFactory newFixture() {
    return modules().fixtureFactory();
  }

  public static EventBus eventBus(){
    return eventBus.get();
  }

  /*public static ObjectSequenceMapBuilder newSequenceMapBuilder(Class<?> type) {
    return new ObjectSequenceMapBuilder(modules().dataKeyMapBuilder().buildMapFrom(modules().fieldExtractor().extractAll(type)));
  }*/

  public static Dependencies modules(){
    return dependencies.get();
  }



  public static class Dependencies {

    public FixtureFactory fixtureFactory() {

      setupRandomSeed();

      FixtureContext fixtureContext = fixtureContext();
      DynamicGeneratorFactory generatorFactory = generatorFactory();
      return new FixtureFactory(null, generationStrategyFactory(fixtureContext), generatorFactory, fixtureContext);
    }

    private void setupRandomSeed() {

      String configuredSeed = System.getProperty("magenta.random.seed");
      Long seed = null;

      if (configuredSeed == null) {
        seed = DEFAULT_SEED_FOR_RANDOM.incrementAndGet();
      } else {
        seed = Long.valueOf(configuredSeed);
      }

      FluentRandom.setRandom(new Random(seed));

      log.info("Seed used by Magenta Fixtures is " + seed);

    }

    public DynamicGeneratorFactory generatorFactory() {
      List<DynamicGeneratorFactory> factories = Lists.newArrayList();

      factories.addAll(PrimitiveDynamicGeneratorFactoryProvider.get());
      factories.add(new ConditionalGeneratorFactory(type -> type.isAssignableFrom(Date.class), () -> FluentRandom.dates().any()));
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


}
