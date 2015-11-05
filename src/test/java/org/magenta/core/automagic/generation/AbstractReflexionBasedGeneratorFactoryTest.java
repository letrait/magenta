package org.magenta.core.automagic.generation;


import java.util.List;

import org.magenta.core.DataKeyMapBuilder;
import org.magenta.core.automagic.generation.provider.ObjectGenerator;
import org.magenta.core.automagic.generation.provider.ObjectGeneratorFactory;
import org.magenta.core.automagic.generation.provider.PrimitiveDynamicGeneratorFactoryProvider;
import org.magenta.core.injector.extractors.HiearchicalFieldsExtractor;

import com.google.common.collect.Lists;

public abstract class AbstractReflexionBasedGeneratorFactoryTest {

  protected DataKeyMapBuilder dataKeyMapBuilder() {
    return new DataKeyMapBuilder(new DataKeyDeterminedFromFieldTypeMappingFunction());
  }
  
  protected DynamicGeneratorFactory buildDynamicGeneratorFactory(){
    
    List<DynamicGeneratorFactory> factories = Lists.newArrayList();
    
    factories.addAll(PrimitiveDynamicGeneratorFactoryProvider.get());
    factories.add(new ObjectGeneratorFactory(HiearchicalFieldsExtractor.SINGLETON, dataKeyMapBuilder()));
    
    return new CompositeGeneratorFactory(factories);
  }

}
