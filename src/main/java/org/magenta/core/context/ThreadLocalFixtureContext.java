package org.magenta.core.context;

import org.magenta.Fixture;
import org.magenta.core.FixtureContext;

public class ThreadLocalFixtureContext implements FixtureContext {

  private ThreadLocal<Fixture> threadLocal = new ThreadLocal<Fixture>();

  @Override
  public Fixture get() {
    return threadLocal.get();

  }

  @Override
  public void set(Fixture fixture) {
    threadLocal.set(fixture);

  }

  @Override
  public void clear() {
   threadLocal.remove();

  }

  @Override
  public boolean isSet() {
   return get()!=null;
  }

}
