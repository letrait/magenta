package org.magenta.core;

import java.util.Iterator;

import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;

public class RoundRobinPickStrategy implements PickStrategy {

  private Iterator<?> iterator;
  private Iterable<?> source;

  @Override
  public <D> D pick(Iterable<D> choices) {
    if(choices!=source){
      source = choices;
      iterator = Iterables.cycle(source).iterator();
    }
    return (D) iterator.next();
  }

  public static Supplier<RoundRobinPickStrategy> supplier(){
    return new Supplier<RoundRobinPickStrategy>(){

      @Override
      public RoundRobinPickStrategy get() {
        return new RoundRobinPickStrategy();
      }

    };
  }

}
