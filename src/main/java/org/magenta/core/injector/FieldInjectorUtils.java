package org.magenta.core.injector;

import java.lang.reflect.Field;

public class FieldInjectorUtils {

  private FieldInjectorUtils(){
    //singleton
  }

  public static void injectInto(Object host, Field f, Object value) {
    try {
      f.setAccessible(true);
      f.set(host, value);
    } catch (IllegalArgumentException | IllegalAccessException e) {
      throw new RuntimeException("Unable to inject " + value + " to the object " + host, e);
    }
  }
}
