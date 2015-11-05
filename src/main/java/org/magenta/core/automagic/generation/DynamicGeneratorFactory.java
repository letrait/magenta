package org.magenta.core.automagic.generation;

import org.magenta.Fixture;
import org.magenta.FixtureFactory;
import org.magenta.core.SimpleGenerationStrategy;
import org.magenta.core.GenerationStrategy;
import org.magenta.core.GenerationStrategyFactory;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.reflect.TypeToken;

public interface DynamicGeneratorFactory {

  public <D>  Optional<GenerationStrategy<D>> buildGeneratorOf(TypeToken<D> type, FixtureFactory fixture, DynamicGeneratorFactory dynamicGeneratorFactory);
}
