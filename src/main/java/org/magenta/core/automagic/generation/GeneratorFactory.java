package org.magenta.core.automagic.generation;

import org.magenta.FixtureFactory;

import com.google.common.base.Supplier;
import com.google.common.reflect.TypeToken;

public interface GeneratorFactory {

  public <D> Supplier<D> buildGeneratorOf(TypeToken<D> type, FixtureFactory fixture);
}
