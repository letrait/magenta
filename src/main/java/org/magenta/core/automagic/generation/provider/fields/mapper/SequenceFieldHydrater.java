package org.magenta.core.automagic.generation.provider.fields.mapper;

import java.lang.reflect.Field;

import org.magenta.Fixture;
import org.magenta.core.automagic.generation.provider.ObjectHydrater;
import org.magenta.core.sequence.ObjectSequenceMap;

import com.google.common.base.Function;
import com.google.inject.internal.Preconditions;

public class SequenceFieldHydrater implements ObjectHydrater {

  private Function<? super Fixture, ObjectSequenceMap> objectSequenceMapOfFixture;


  public SequenceFieldHydrater(Function<? super Fixture, ObjectSequenceMap> objectSequenceMapOfFixture) {
    this.objectSequenceMapOfFixture = Preconditions.checkNotNull(objectSequenceMapOfFixture);
  }

  @Override
  public void hydrate(Object candidate, Fixture fixture) throws IllegalArgumentException, IllegalAccessException {

    ObjectSequenceMap map = objectSequenceMapOfFixture.apply(fixture);

    for(Field f: map.fields()){
      f.setAccessible(true);
      f.set(candidate, map.get(f).next());
    }

  }

}
