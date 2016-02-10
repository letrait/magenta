package org.magenta.core.automagic.generation;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.magenta.DataKey;

import com.google.common.base.Function;
import com.google.common.reflect.TypeToken;

public class DataKeyDeterminedFromFieldTypeMappingFunction implements Function<Field,DataKey<?>>{

  @Override
  public DataKey<?> apply(Field input) {

    Type type = input.getGenericType();

    if(Iterable.class.isAssignableFrom(input.getType())) {
      ParameterizedType pt = (ParameterizedType)input.getGenericType();
      type= pt.getActualTypeArguments()[0];
    }else if(input.getType().isArray()){
      throw new UnsupportedOperationException("Cannot inject into array for now");
    }

    TypeToken<?> token = TypeToken.of(type);

    DataKey<?> key = DataKey.of(format(input.toGenericString()), token, true);

    return key;
  }

  private String format(String genericString) {
    return genericString.substring(genericString.lastIndexOf(' ')+1);
  }

}
