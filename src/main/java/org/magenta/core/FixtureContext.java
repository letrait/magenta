package org.magenta.core;

import org.magenta.Fixture;

import com.google.common.base.Supplier;

public interface FixtureContext extends Supplier<Fixture>{

  public void set(Fixture fixture);
  public void clear();
  public boolean isSet();

}
