package org.magenta.core;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.magenta.DataKey;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

public class DataKeyMapBuilder {

  private Function<? super Field, DataKey<?>> mappingFunction;

  public DataKeyMapBuilder(Function<? super Field, DataKey<?>> mappingFunction) {
    this.mappingFunction = mappingFunction;
  }

  public Map<Field, DataKey<?>> buildMapFrom(List<Field> fields) {
    Map<Field, DataKey<?>> dataKeyMap = Maps.newHashMap();

    for(Field f:fields){
      dataKeyMap.put(f, mappingFunction.apply(f));
    }

    return dataKeyMap;
  }

}
