package org.magenta.core.automagic.generation.provider;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.magenta.DataKey;
import org.magenta.FixtureFactory;
import org.magenta.core.GenerationStrategy;
import org.magenta.core.automagic.generation.DynamicGeneratorFactory;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.base.Optional;
import com.google.common.base.Suppliers;

@RunWith(MockitoJUnitRunner.class)
public class ConditionalGeneratorProviderTest {

  @Mock
  private FixtureFactory fixture;

  @Mock
  private DynamicGeneratorFactory generatorFactory;

  @Test
  public void testAGeneratorIsProvidedWhenTheTypeIsString(){

    ConditionalGeneratorFactory sut = new ConditionalGeneratorFactory( type -> type.isSubtypeOf(String.class), Suppliers.ofInstance("abcd"));

    //exercise sut
    Optional<GenerationStrategy<String>> actual = sut.buildGeneratorOf(DataKey.of(String.class), fixture, generatorFactory);

    //verify outcome
    assertThat(actual.isPresent()).isTrue();

  }

  @Test
  public void testAGeneratorIsNotProvidedWhenTheTypeIsNotString(){

    ConditionalGeneratorFactory sut = new ConditionalGeneratorFactory(type -> type.isSubtypeOf(String.class), Suppliers.ofInstance("abcd"));

    //exercise sut
    Optional<GenerationStrategy<String>> actual = sut.buildGeneratorOf((DataKey)DataKey.of(Integer.class), fixture, generatorFactory);

    //verify outcome
    assertThat(actual.isPresent()).isFalse();

  }
}
