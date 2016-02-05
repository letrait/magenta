package org.magenta.core.automagic.generation;


import org.magenta.Magenta;
import org.magenta.core.DataKeyMapBuilder;

public abstract class AbstractDynamicGeneratorFactoryTest {

  protected DataKeyMapBuilder dataKeyMapBuilder() {
    return new DataKeyMapBuilder(new DataKeyDeterminedFromFieldTypeMappingFunction());
  }
  
  protected DynamicGeneratorFactory buildDynamicGeneratorFactory(){
    return Magenta.modules().generatorFactory();
  }

}
