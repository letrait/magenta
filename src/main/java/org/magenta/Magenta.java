package org.magenta;

import java.util.List;

import org.assertj.core.util.Lists;
import org.magenta.core.DataKeyMapBuilder;
import org.magenta.core.FixtureContext;
import org.magenta.core.GenerationStrategyFactory;
import org.magenta.core.Injector;
import org.magenta.core.automagic.generation.DataKeyDeterminedFromFieldTypeMappingFunction;
import org.magenta.core.automagic.generation.GeneratorFactory;
import org.magenta.core.automagic.generation.ReflexionBasedGeneratorFactory;
import org.magenta.core.context.ThreadLocalFixtureContext;
import org.magenta.core.injector.FieldInjectionChainProcessor;
import org.magenta.core.injector.FieldInjectionHandler;
import org.magenta.core.injector.FieldsExtractor;
import org.magenta.core.injector.extractors.HiearchicalFieldsExtractor;
import org.magenta.core.injector.handlers.DataSetFieldHandler;
import org.magenta.core.injector.handlers.SequenceFieldHandler;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

public class Magenta {

  public static FixtureFactory newFixture() {
    return dependencies.get().fixtureFactory();
  }

  public static final Supplier<Dependencies> dependencies = Suppliers.memoize(new Supplier<Dependencies>() {

    @Override
    public Dependencies get() {
      return new Dependencies();
    }

  });

  public static class Dependencies {

    public FixtureFactory fixtureFactory() {
      FixtureContext fixtureContext = fixtureContext();
      GeneratorFactory generatorFactory = generatorFactory(fixtureContext);
      return new FixtureFactory(null, generationStrategyFactory(fixtureContext), generatorFactory, fixtureContext);
    }

    public GeneratorFactory generatorFactory(FixtureContext context) {
      return new ReflexionBasedGeneratorFactory(context, dataKeyMapBuilder(), fieldExtractor());
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
