package org.magenta.core.injector;

import org.magenta.Fixture;

import com.google.common.base.Supplier;

public interface FieldInjectionHandler {

  public void injectInto(Object target, Supplier<? extends Fixture> fixture);


}
