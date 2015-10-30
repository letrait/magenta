package org.magenta.core.injector;

import java.util.Map;

import org.magenta.Fixture;
import org.magenta.core.Injector;

import com.google.common.base.Supplier;

public interface FieldInjectionHandler {

  public Map<Injector.Key, Object> injectInto(Object target, Supplier<? extends Fixture> fixture);


}
