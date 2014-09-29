package org.magenta.core.injection;

import java.util.concurrent.Callable;

import org.magenta.DataSpecification;
import org.magenta.Fixture;

public class ThreadLocalDataDomainSupplier<S extends DataSpecification> implements FixtureContext<S> {

  private ThreadLocal<Fixture> current = new ThreadLocal<Fixture>();


  protected void setContext(Fixture<? extends S> domain) {
    current.set(domain);
  }

  protected void removeContext() {
    current.set(null);
  }

  @Override
  public Fixture<S> get() {
    return current.get();
  }

  @Override
  public <D> D execute(Callable<D> callable, Fixture<? extends S> fixture) {

    if (get() != null && !fixture.equals(get())) {
      throw new IllegalStateException("Illegal attemp to set a new context when one is already active");
    }

    boolean responsible = false;

    try {
      if (get() == null) {
        responsible = true;
        setContext(fixture);
      }
      D data = uncheckedCall(callable);

      return data;
    } finally {
      if (responsible) {
        removeContext();
      }
    }
  }

  private <D> D uncheckedCall(Callable<D> callable) {
    try {
      return callable.call();
    } catch (RuntimeException re) {
      throw re;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void post(Object event) {

    get().getEventBus().post(event);

  }

}
