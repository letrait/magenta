package org.magenta.core.automagic.generation.provider.fields.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import org.magenta.DataKey;
import org.magenta.DataSet;
import org.magenta.Fixture;
import org.magenta.core.automagic.generation.provider.ObjectHydrater;
import org.magenta.core.injector.extractors.HiearchicalFieldsExtractor;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;

public class DataSetFieldHydrater implements ObjectHydrater {

  @Override
  public void hydrate(Object candidate, Fixture fixture) throws IllegalArgumentException, IllegalAccessException {

    HiearchicalFieldsExtractor.SINGLETON.extractAll(candidate.getClass()).stream().filter(f -> Iterable.class.isAssignableFrom(f.getType()))
    .forEach(f -> setValue(f, candidate, fixture));

  }

  private void setValue(Field f, Object candidate, Fixture fixture) {

    List<DataKey<?>> keys = Lists.newArrayList(fixture.keys());


    if (f.getType().isAssignableFrom(List.class)) {
      ParameterizedType pt = (ParameterizedType) f.getGenericType();
      Type type = pt.getActualTypeArguments()[0];
      TypeToken<?> token = TypeToken.of(type);

      DataKey<?> key = DataKey.of(format(f.toGenericString()), token, true);

      key = generalize(key, keys);

      DataSet ds = fixture.dataset(key);
      try {
        f.setAccessible(true);
        f.set(candidate, ds.randomList(5));
      } catch (Exception e) {
        //TODO 1: handle this
        e.printStackTrace();
      }
    } else if (f.getType().isAssignableFrom(Set.class)) {
      ParameterizedType pt = (ParameterizedType) f.getGenericType();
      Type type = pt.getActualTypeArguments()[0];
      TypeToken<?> token = TypeToken.of(type);

      DataKey<?> key = DataKey.of(format(f.toGenericString()), token, true);

      key = generalize(key, keys);

      DataSet ds = fixture.dataset(key);
      try {
        f.setAccessible(true);
        f.set(candidate, ds.set(5));
      } catch (Exception e) {
        //TODO 1: handle this
      }
    } else {
      throw new UnsupportedOperationException("type " + f.getType() + " is not supported");
    }
  }

  private DataKey<?> generalize(DataKey<?> datakey, List<DataKey<?>> keys) {
    if (!datakey.isGeneralized() && datakey.isGeneralizable()) {
      int i1 = keys.indexOf(datakey);
      int i2 = keys.indexOf(datakey.generalize());

      if (i2 != -1 && (i1 == -1 || i2 < i1)) {
        datakey = datakey.generalize();
      }
    }
    return datakey;
  }

  // This is a copy of DataKeyDeterminedFromFieldTypeFunction
  private String format(String genericString) {
    return genericString.substring(genericString.lastIndexOf(' ') + 1);
  }

}
