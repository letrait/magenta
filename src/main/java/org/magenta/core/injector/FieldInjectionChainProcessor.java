package org.magenta.core.injector;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.magenta.Fixture;
import org.magenta.core.Injector;

import com.google.common.base.Supplier;

public class FieldInjectionChainProcessor implements Injector{

  private Supplier<Fixture> fixtureReference;
  private List<? extends FieldInjectionHandler> handlers;


  public FieldInjectionChainProcessor(List<FieldInjectionHandler> handlers, Supplier<Fixture> fixtureReference){
    this.handlers = checkNotNull(handlers);
    this.fixtureReference = checkNotNull(fixtureReference);
  }

  @Override
  public <O> O inject(O o) {

      for(FieldInjectionHandler handler:handlers){
        handler.injectInto(o,fixtureReference);
      }

    return o;
  }

}
