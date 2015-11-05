package org.magenta.core;


import java.util.Map;

import org.magenta.Fixture;
import org.magenta.core.data.supplier.ContextualizedSupplierFunctionAdapter;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

public class GenerationStrategyFactory {
  
  private final FixtureContext fixtureContext;
  private final Injector injector;
 
  
  public GenerationStrategyFactory(FixtureContext fixtureContext, Injector injector){
    this.fixtureContext = fixtureContext;
    this.injector = injector;
    
  }
  
  public <D> GenerationStrategy<D> create(Supplier<D> generator){
    
    Map<Injector.Key<?>, Object> injectionResults = injector.inject(generator);
    
    final Function<Fixture,Integer> generatorSizeFunction = Injector.Key.NUMBER_OF_COMBINATION_FUNCTION.getFrom(injectionResults);
    
    return new SimpleGenerationStrategy<>(
        new ContextualizedSupplierFunctionAdapter<>(generator, fixtureContext), 
        generatorSizeFunction);
  }
  

}
