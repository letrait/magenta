package org.magenta.core;

import java.util.concurrent.Callable;

import org.magenta.CycleDetectedInGenerationException;
import org.magenta.DataDomain;
import org.magenta.DataKey;
import org.magenta.DataSpecification;
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
  public <D> Iterable<D> execute(Callable<Iterable<D>> callable, DataDomain<? extends S> fixture) {

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
  public DataDomain<S> get() {
    return context.get();
  }
}
