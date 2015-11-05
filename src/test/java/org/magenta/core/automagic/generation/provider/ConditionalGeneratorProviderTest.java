package org.magenta.core.automagic.generation.provider;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.magenta.Fixture;
import org.magenta.FixtureFactory;
import org.magenta.core.GenerationStrategy;
import org.magenta.core.SimpleGenerationStrategy;
import org.magenta.core.automagic.generation.DynamicGeneratorFactory;
import org.magenta.core.automagic.generation.provider.ConditionalGeneratorFactory;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.reflect.TypeToken;

@RunWith(MockitoJUnitRunner.class)
public class ConditionalGeneratorProviderTest {

  @Mock
  private FixtureFactory fixture;
  
  @Mock
  private DynamicGeneratorFactory generatorFactory;
  
  @Test
  public void testAGeneratorIsProvidedWhenTheTypeIsString(){
    
    ConditionalGeneratorFactory sut = new ConditionalGeneratorFactory( type -> type.isAssignableFrom(String.class), Suppliers.ofInstance("abcd"));
    
    //exercise sut
    Optional<GenerationStrategy<String>> actual = sut.buildGeneratorOf(TypeToken.of(String.class), fixture, generatorFactory);
    
    //verify outcome
    assertThat(actual.isPresent()).isTrue();
    
  }
  
  @Test
  public void testAGeneratorIsNotProvidedWhenTheTypeIsNotString(){
    
    ConditionalGeneratorFactory sut = new ConditionalGeneratorFactory(type -> type.isAssignableFrom(String.class), Suppliers.ofInstance("abcd"));
    
    //exercise sut
    Optional<GenerationStrategy<String>> actual = sut.buildGeneratorOf((TypeToken)TypeToken.of(Integer.class), fixture, generatorFactory);
    
    //verify outcome
    assertThat(actual.isPresent()).isFalse();
    
  }
}
