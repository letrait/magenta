package org.magenta.core;

import org.magenta.Fixture;

public interface GenerationStrategy<D> {

  D generate(Fixture fixture);

  Integer size(Fixture fixture);

}