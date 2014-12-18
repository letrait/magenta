package org.magenta.core;

import org.magenta.random.FluentRandom;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

public class RandomPickStrategy implements PickStrategy{

  private FluentRandom fluentRandom;
  private static final RandomPickStrategy singleton = new RandomPickStrategy(FluentRandom.singleton());

  public RandomPickStrategy(FluentRandom fluentRandom){
    this.fluentRandom = fluentRandom;
  }

  @Override
  public <D> D pick(Iterable<D> choices) {
    return fluentRandom.iterable(choices).any();
  }

  public static Supplier<RandomPickStrategy> supplier(FluentRandom fluentRandom){
    return Suppliers.ofInstance(new RandomPickStrategy(fluentRandom));
  }

  public static RandomPickStrategy singleton() {
    return singleton;
  }

}
