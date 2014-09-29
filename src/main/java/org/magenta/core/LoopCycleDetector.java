package org.magenta.core;

import java.util.concurrent.Callable;

import org.magenta.CycleDetectedInGenerationException;
import org.magenta.DataKey;
import org.magenta.DataSpecification;
import org.magenta.Fixture;
import org.magenta.core.injection.FixtureContext;

public class LoopCycleDetector<S extends DataSpecification> implements FixtureContext<S> {

  private boolean active;
  private FixtureContext<S> context;
  private DataKey<?> key;

  public LoopCycleDetector(FixtureContext<S> context, DataKey<?> key) {
    this.context = context;
    this.key = key;
  }

  @Override
  public <D> D execute(Callable<D> callable, Fixture<? extends S> fixture) {

    try {
      if (active) {
        throw new CycleDetectedInGenerationException("Infinite loop detected for generation of key " + key);
      }
      active = true;

      return context.execute(callable, fixture);
    } finally {
      active = false;
    }
  }


  @Override
  public Fixture<S> get() {
    return context.get();
  }

  @Override
  public void post(Object event) {
    context.post(event);
  }


}
