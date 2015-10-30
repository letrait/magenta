package org.magenta.core;

import java.util.Map;


public interface Injector {

  public enum Key {
    NUMBER_OF_COMBINATION_FUNCTION;
  }
  
  public Map<Key,Object> inject(Object o);

}