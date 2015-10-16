package org.magenta.core.automagic.generation;

import java.lang.reflect.Field;

import org.magenta.DataKey;

import com.google.common.base.Function;
import com.google.common.reflect.TypeToken;

public class DataKeyDeterminedFromFieldTypeMappingFunction implements Function<Field,DataKey<?>>{

  @Override
  public DataKey<?> apply(Field input) {

    TypeToken<?> token = TypeToken.of(input.getGenericType());

    DataKey<?> key = DataKey.of(format(input.toGenericString()), token, true);

    return key;
  }

  private String format(String genericString) {
    return genericString.substring(genericString.lastIndexOf(' ')+1);
  }

}
