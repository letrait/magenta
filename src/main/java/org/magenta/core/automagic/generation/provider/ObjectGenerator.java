package org.magenta.core.automagic.generation.provider;

import java.lang.reflect.Constructor;
import java.util.List;

import org.magenta.DataGenerationException;
import org.magenta.Fixture;
import org.magenta.core.GenerationStrategy;
import org.magenta.core.sequence.ObjectSequenceMap;

import com.google.common.base.Function;
import com.google.common.reflect.TypeToken;

public class ObjectGenerator<D> implements GenerationStrategy<D> {

  private final TypeToken<D> type;
  private final Function<Fixture, ObjectSequenceMap> sequenceProvider;
  private final List<ObjectHydrater> hydraters;

  public ObjectGenerator(TypeToken<D> type,  Function<Fixture, ObjectSequenceMap> sequenceProvider, List<ObjectHydrater> hydraters) {
    this.type = type;
    this.sequenceProvider = sequenceProvider;
    this.hydraters = hydraters;

  }

  @Override
  public D generate(Fixture fixture) {
    try {

      Constructor<? super D> c = type.getRawType().getDeclaredConstructor();
      c.setAccessible(true);
      D candidate = type.constructor(c).invoke(null);

      for(ObjectHydrater hydrater:hydraters){
        hydrater.hydrate(candidate, fixture);
      }

      return candidate;
    } catch (Exception e) {
      throw new DataGenerationException(String.format("Error while generating %s", type.getRawType()), e);
    }
  }



  @Override
  public Integer size(Fixture fixture) {
    return sequenceProvider.apply(fixture).getCombinationCount();
  }

}
