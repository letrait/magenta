package org.magenta.core.automagic.generation;

import org.magenta.core.DataKeyMapBuilder;

public abstract class AbstractReflexionBasedGeneratorFactoryTest {

  protected DataKeyMapBuilder dataKeyMapBuilder() {
    return new DataKeyMapBuilder(new DataKeyDeterminedFromFieldTypeMappingFunction());
  }

}
