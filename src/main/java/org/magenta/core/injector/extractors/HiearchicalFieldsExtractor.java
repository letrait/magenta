package org.magenta.core.injector.extractors;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.magenta.core.injector.FieldsExtractor;

import com.google.common.collect.Lists;

public enum HiearchicalFieldsExtractor implements FieldsExtractor {

  SINGLETON;

  @Override
  public List<Field> extractAll(Class<? extends Object> input) {
    List<Field> fields = Lists.newArrayList();

    Class<? extends Object> c = input;

    while (c != null) {
      Field[] f = c.getDeclaredFields();
      fields.addAll(Arrays.asList(f));
      c = c.getSuperclass();
      if (c == Object.class) {
        c = null;
      }
    }

    return fields;
  }

}
