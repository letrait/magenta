package org.magenta.core.injection;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import org.magenta.DataSpecification;
import org.magenta.Fixture;


public interface FixtureContext<S extends DataSpecification> extends Supplier<Fixture<S>> {

  public <D> D execute(Callable<D> callable, Fixture<? extends S> fixture);

  public void post(Object event);


}
