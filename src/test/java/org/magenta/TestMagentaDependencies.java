package org.magenta;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.magenta.Fixture;
import org.magenta.Magenta;
import org.magenta.core.injector.FieldInjectionHandler;
import org.mockito.Mockito;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

public class TestMagentaDependencies {

  @Test
  public void testGenerationStrategyBuilder(){
    assertThat(Magenta.dependencies.get().generationStrategyFactory(Magenta.dependencies.get().fixtureContext())).isNotNull();
  }

  @Test
  public void testFixtureContext(){
    Supplier<Fixture> sut = Magenta.dependencies.get().fixtureContext();
    assertThat(sut).isNotNull();

  }
}
