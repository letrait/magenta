package org.magenta;

import java.util.ArrayList;
import java.util.List;

import org.magenta.core.DataKeyMapBuilder;
import org.magenta.core.FixtureContext;
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

  public static final Supplier<Dependencies> dependencies = Suppliers.memoize(new Supplier<Dependencies>(){

    @Override
    public Dependencies get() {
     return new Dependencies();
    }

  });

  public static class Dependencies {

    public FixtureFactory fixtureFactory(){
      FixtureContext fixtureContext = fixtureContext();
      GeneratorFactory generatorFactory = generatorFactory(fixtureContext);
      return new FixtureFactory(null, injector(fixtureContext), generatorFactory, fixtureContext);
    }

    public GeneratorFactory generatorFactory(FixtureContext context) {
      return new ReflexionBasedGeneratorFactory(context, dataKeyMapBuilder(), fieldExtractor());
    }

    public DataKeyMapBuilder dataKeyMapBuilder() {
      return new DataKeyMapBuilder(new DataKeyDeterminedFromFieldTypeMappingFunction());
    }

    public Injector injector(Supplier<Fixture> supplier){
      FieldInjectionChainProcessor injector = new FieldInjectionChainProcessor(fieldInjectionHandlers(), supplier);
      return injector;

    }

    //this could be static, no need to recreated each time
    List<FieldInjectionHandler> fieldInjectionHandlers(){

      List<FieldInjectionHandler> handlers = new ArrayList<>();
      handlers.add(sequenceFieldHandler());
      handlers.add(dataSetFieldHandler());
      return handlers;
    }


    private DataSetFieldHandler dataSetFieldHandler() {
      return new DataSetFieldHandler(fieldExtractor());
    }

    private SequenceFieldHandler sequenceFieldHandler() {
      return new SequenceFieldHandler(fieldExtractor());
    }

    private FieldsExtractor fieldExtractor(){
      return HiearchicalFieldsExtractor.SINGLETON;
    }

    FixtureContext fixtureContext(){
      return new ThreadLocalFixtureContext();
    }
  }
}
