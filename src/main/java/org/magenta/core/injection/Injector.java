package org.magenta.core.injection;

public interface Injector {

  public <O> O inject(O o);

}