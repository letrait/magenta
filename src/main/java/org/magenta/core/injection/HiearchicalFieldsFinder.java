package org.magenta.core.injection;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import com.google.common.collect.Lists;


public enum HiearchicalFieldsFinder implements Function<Class<?>,List<Field>> {

  SINGLETON;

  @Override
  public List<Field> apply(Class<?> input) {

    List<Field> fields = Lists.newArrayList();

    Class<? extends Object> c = input;

     while(c!=null){
       Field[] f = c.getDeclaredFields();
       fields.addAll(Arrays.asList(f));
       c = c.getSuperclass();
       if(c == Object.class){
         c = null;
       }
     }

     return fields;
  }

}
