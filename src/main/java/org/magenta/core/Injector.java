package org.magenta.core;

public interface Injector {

  public <O> O inject(O o);

}