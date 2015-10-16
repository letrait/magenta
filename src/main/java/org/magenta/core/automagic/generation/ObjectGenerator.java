package org.magenta.core.automagic.generation;

import java.lang.reflect.Field;

import org.magenta.DataGenerationException;
import org.magenta.Fixture;
import org.magenta.core.sequence.SequenceIndexMap;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.reflect.TypeToken;


//TODO this should probably implements Sequence
public class ObjectGenerator<D> implements Supplier<D>{

  private final TypeToken<D> type;
  private final Function<Fixture, SequenceIndexMap> sequenceProvider;
  private final Supplier<? extends Fixture> fixtureSupplier;


  public ObjectGenerator(TypeToken<D> type, Supplier<? extends Fixture> fixtureSupplier, Function<Fixture, SequenceIndexMap> sequenceProvider) {
    this.type = type;
    this.fixtureSupplier = fixtureSupplier;
    this.sequenceProvider = sequenceProvider;
  }


  @Override
  public D get() {
    try{
    D candidate = (D) type.getRawType().newInstance();

    Fixture current = fixtureSupplier.get();

    SequenceIndexMap sequences = sequenceProvider.apply(current);

    for(Field f:sequences.fields()){
      f.setAccessible(true);
      f.set(candidate, sequences.get(f).get());
    }

    return candidate;
    } catch (Exception e) {
      throw new DataGenerationException(String.format("Error while generating %s", type), e);
    }
  }

}
