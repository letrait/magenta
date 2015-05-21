package org.magenta.core.automagic.generation;

import com.google.common.base.Supplier;
import com.google.common.reflect.TypeToken;

public interface GeneratorFactory {

  public <D> Supplier<D> buildGeneratorOf(TypeToken<D> type);
}
