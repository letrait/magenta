package org.magenta;

import java.util.Date;
import java.util.List;

import org.magenta.core.DataKeyMapBuilder;
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
import org.magenta.core.sequence.ObjectSequenceMapBuilder;
import org.magenta.random.FluentRandom;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;

public class Magenta {
  
  private static final Supplier<Dependencies> dependencies = Suppliers.memoize(new Supplier<Dependencies>() {

    @Override
    public Dependencies get() {
      return new Dependencies();
    }

  });

  public static FixtureFactory newFixture() {
    return modules().fixtureFactory();
  }
  
  public static ObjectSequenceMapBuilder newSequenceMapBuilder(Class<?> type) {
    return new ObjectSequenceMapBuilder(modules().dataKeyMapBuilder().buildMapFrom(modules().fieldExtractor().extractAll(type)));
  }

  public static Dependencies modules(){
    return dependencies.get();
  }
  
  

  public static class Dependencies {

    public FixtureFactory fixtureFactory() {
      FixtureContext fixtureContext = fixtureContext();
      DynamicGeneratorFactory generatorFactory = generatorFactory();
      return new FixtureFactory(null, generationStrategyFactory(fixtureContext), generatorFactory, fixtureContext);
    }

    public DynamicGeneratorFactory generatorFactory() {
      List<DynamicGeneratorFactory> factories = Lists.newArrayList();
      
      factories.addAll(PrimitiveDynamicGeneratorFactoryProvider.get());
      factories.add(new ConditionalGeneratorFactory(type -> type.isAssignableFrom(Date.class), () -> FluentRandom.dates().any()));
      factories.add(new ObjectGeneratorFactory( fieldExtractor(), dataKeyMapBuilder()));
     
      
      return new CompositeGeneratorFactory(factories);
    }

    public DataKeyMapBuilder dataKeyMapBuilder() {
      return new DataKeyMapBuilder(new DataKeyDeterminedFromFieldTypeMappingFunction());
    }

    public GenerationStrategyFactory generationStrategyFactory(FixtureContext fixtureContext) {
      return new GenerationStrategyFactory(fixtureContext, injector(fixtureContext));
    }
    
    public Injector injector(FixtureContext fixtureContext){
      return new FieldInjectionChainProcessor(fieldInjectionHandlers(), fixtureContext);
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
