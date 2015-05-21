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
  public void testInjector(){
    assertThat(Magenta.dependencies.get().injector(Suppliers.ofInstance(Mockito.mock(Fixture.class)))).isNotNull();
  }

  @Test
  public void testFieldInjectionHandlers(){
    List<FieldInjectionHandler> handlers = Magenta.dependencies.get().fieldInjectionHandlers();
    assertThat(handlers).isNotNull().isNotEmpty();
  }

  @Test
  public void testFixtureContext(){
    Supplier<Fixture> sut = Magenta.dependencies.get().fixtureContext();
    assertThat(sut).isNotNull();

  }
}
