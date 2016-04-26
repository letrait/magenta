package org.magenta.core;

import java.util.Map;

import org.magenta.Fixture;

import com.google.common.base.Function;
import com.google.common.base.Supplier;


public interface Injector {

  public static class Key<A> {
    public static final Key<Function<Fixture,Integer>> NUMBER_OF_COMBINATION_FUNCTION = new Key<>("NUMBER_OF_COMBINATION_FUNCTION");
    private final String name;

    public Key(String name) {
      this.name = name;
    }

    public A getFrom(Map<Key<?>,Object> result){
      return (A)result.get(this);
    }
  }

  public Map<Key<?>,Object> inject(Object o, Supplier<Fixture> fixtureReference);

}