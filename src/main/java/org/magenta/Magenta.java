package org.magenta;

import java.util.ArrayList;
import java.util.List;

import org.magenta.core.FixtureContext;
import org.magenta.core.Injector;
import org.magenta.core.automagic.generation.GeneratorFactory;
import org.magenta.core.automagic.generation.ReflexionBasedGeneratorFactory;
import org.magenta.core.context.ThreadLocalFixtureContext;
import org.magenta.core.injector.FieldInjectionChainProcessor;
import org.magenta.core.injector.FieldInjectionHandler;
import org.magenta.core.injector.FieldsExtractor;
import org.magenta.core.injector.extractors.HiearchicalFieldsExtractor;
import org.magenta.core.injector.handlers.DataSetFieldHandler;
import org.magenta.core.injector.handlers.SequenceFieldHandler;
import org.magenta.random.FluentRandom;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

public class Magenta {

  public static FixtureFactory newFixture(FluentRandom random) {
    return dependencies.get().fixtureFactory(random);
  }

  static final Supplier<Dependencies> dependencies = Suppliers.memoize(new Supplier<Dependencies>(){

    @Override
    public Dependencies get() {
     return new Dependencies();
    }

  });

  static class Dependencies {

    public FixtureFactory fixtureFactory(FluentRandom random){
      FixtureContext fixtureContext = fixtureContext();
      GeneratorFactory generatorFactory = generatorFactory(random);
      return new FixtureFactory(null, random, injector(fixtureContext), generatorFactory, fixtureContext);
    }

    private GeneratorFactory generatorFactory(FluentRandom random) {
      return new ReflexionBasedGeneratorFactory(random, fieldExtractor());
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
