package org.magenta.core.sequence;

import java.lang.reflect.Field;
import java.util.Map;

import org.magenta.Sequence;

import com.google.common.collect.Maps;

public class SequenceIndexMap {

  private Map<Field,Sequence<?>> map = Maps.newLinkedHashMap();

  public void put(Field field, Sequence<?> expected) {
    map.put(field, expected);

  }

  public Sequence<?> get(Field field) {
    return map.get(field);
  }

  public boolean isEmpty() {
    return map.isEmpty();
  }

}
