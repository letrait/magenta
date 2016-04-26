package org.magenta.core.automagic.generation;

import org.magenta.FixtureFactory;
import org.magenta.core.GenerationStrategy;

import com.google.common.base.Optional;
import com.google.common.reflect.TypeToken;

public interface DynamicGeneratorFactory {

  public <D>  Optional<? extends GenerationStrategy<D>> buildGeneratorOf(TypeToken<D> type, FixtureFactory fixture, DynamicGeneratorFactory dynamicGeneratorFactory);
}
