package org.magenta.core.automagic.generation.provider;

import org.magenta.Fixture;

public interface ObjectHydrater {

  public void hydrate(Object candidate, Fixture fixture) throws IllegalArgumentException, IllegalAccessException;
}
