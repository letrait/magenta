package org.magenta;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.google.common.base.Supplier;

public class TestMagentaDependencies {

  @Test
  public void testGenerationStrategyBuilder(){
    assertThat(Magenta.modules().generationStrategyFactory(Magenta.modules().fixtureContext())).isNotNull();
  }

  @Test
  public void testFixtureContext(){
    Supplier<Fixture> sut = Magenta.modules().fixtureContext();
    assertThat(sut).isNotNull();

  }
}
