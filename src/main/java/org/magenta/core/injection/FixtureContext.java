package org.magenta.core.injection;

import java.util.concurrent.Callable;

import org.magenta.DataSpecification;
import org.magenta.Fixture;

import com.google.common.base.Supplier;

public interface FixtureContext<S extends DataSpecification> extends Supplier<Fixture<S>> {

  public <D> D execute(Callable<D> callable, Fixture<? extends S> fixture);

  public void post(Object event);


}
