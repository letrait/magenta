package org.magenta.core.automagic.generation;


import org.magenta.Magenta;

public abstract class AbstractDynamicGeneratorFactoryTest {


  protected DynamicGeneratorFactory buildDynamicGeneratorFactory(){
    return Magenta.modules().generatorFactory();
  }

}
