package org.magenta.core.automagic.generation.provider;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.magenta.DataGenerationException;
import org.magenta.Fixture;
import org.magenta.core.GenerationStrategy;
import org.magenta.core.sequence.ObjectSequenceMap;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.reflect.TypeToken;

public class ObjectGenerator<D> implements GenerationStrategy<D> {

  private final TypeToken<D> type;
  private final Function<Fixture, ObjectSequenceMap> sequenceProvider;

  public ObjectGenerator(TypeToken<D> type,  Function<Fixture, ObjectSequenceMap> sequenceProvider) {
    this.type = type;
    this.sequenceProvider = sequenceProvider;

  }

  @Override
  public D generate(Fixture fixture) {
    try {

      Constructor<? super D> c = type.getRawType().getConstructor();

      D candidate = (D) type.constructor(c).invoke(null);

      ObjectSequenceMap sequences = sequenceProvider.apply(fixture);
      

      for (Field f : sequences.fields()) {
        f.setAccessible(true);
        f.set(candidate, sequences.get(f).get());
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
