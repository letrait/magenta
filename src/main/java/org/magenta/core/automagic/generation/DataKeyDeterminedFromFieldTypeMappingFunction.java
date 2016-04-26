package org.magenta.core.automagic.generation;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.magenta.DataKey;
import org.magenta.core.sequence.FieldSequenceDefinition;

import com.google.common.base.Function;
import com.google.common.reflect.TypeToken;

public class DataKeyDeterminedFromFieldTypeMappingFunction implements Function<Field,FieldSequenceDefinition>{

  @Override
  public FieldSequenceDefinition apply(Field input) {

    Type type = input.getGenericType();

    FieldSequenceDefinition.Type ftype =  FieldSequenceDefinition.Type.ATTRIBUTE;

    if(Iterable.class.isAssignableFrom(input.getType())) {
      ParameterizedType pt = (ParameterizedType)input.getGenericType();
      type= pt.getActualTypeArguments()[0];
      ftype =  FieldSequenceDefinition.Type.ITERABLE;
    }else if(input.getType().isArray()){
      throw new UnsupportedOperationException("Cannot inject into array for now");
    }

    TypeToken<?> token = TypeToken.of(type);

    DataKey<?> key = DataKey.of(format(input.toGenericString()), token, true);

    return FieldSequenceDefinition.make(input, key, ftype);
  }

  private String format(String genericString) {
    return genericString.substring(genericString.lastIndexOf(' ')+1);
  }

}
