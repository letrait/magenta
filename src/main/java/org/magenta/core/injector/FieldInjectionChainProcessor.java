package org.magenta.core.injector;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import org.magenta.Fixture;
import org.magenta.core.Injector;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;

public class FieldInjectionChainProcessor implements Injector {

  private List<? extends FieldInjectionHandler> handlers;

  public FieldInjectionChainProcessor(List<FieldInjectionHandler> handlers) {
    this.handlers = checkNotNull(handlers);
  }

  @Override
  public Map<Injector.Key<?>, Object> inject(Object o, Supplier<Fixture> fixtureReference) {
    Map<Injector.Key<?>, Object> injectionResults = Maps.newHashMap();

    for (FieldInjectionHandler handler : handlers) {
      injectionResults.putAll(handler.injectInto(o, fixtureReference));
    }

    return injectionResults;
  }

}
