package org.magenta.core.injection;

import java.util.concurrent.Callable;

import org.magenta.DataDomain;
import org.magenta.DataSpecification;

import com.google.common.base.Supplier;

public interface FixtureContext<S extends DataSpecification> extends Supplier<DataDomain<S>> {

  /*public void setContext(DataDomain<? extends DataSpecification> domain);

  public void removeContext();*/

  public <D> Iterable<D> execute(Callable<Iterable<D>> callable, DataDomain<? extends S> fixture);


}
