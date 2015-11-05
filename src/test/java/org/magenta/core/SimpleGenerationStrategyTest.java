package org.magenta.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.magenta.Fixture;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.base.Function;

@RunWith(MockitoJUnitRunner.class)
public class SimpleGenerationStrategyTest {

  @Mock
  private Fixture fixture;

  @Test
  public void test_generator_function_is_used(){

    //setup fixtures
    Function<Fixture,String>  generator = mock(Function.class);
    Function<Fixture,Integer>  sizeCalcualtor = mock(Function.class);
    
    when(generator.apply(fixture)).thenReturn("foo");

    GenerationStrategy<String> sut = new SimpleGenerationStrategy<String>(generator, sizeCalcualtor);

    //exercise sut
    String actual = sut.generate(fixture);
    

    //verify outcome
    assertThat(actual).isEqualTo("foo");

    

  }
  
  @Test
  public void test_size_calculator_function_is_used(){

    //setup fixtures
    Function<Fixture,String>  generator = mock(Function.class);
    Function<Fixture,Integer>  sizeCalcualtor = mock(Function.class);
    
    when(sizeCalcualtor.apply(fixture)).thenReturn(1234);

    GenerationStrategy<String> sut = new SimpleGenerationStrategy<String>(generator, sizeCalcualtor);

    //exercise sut
    Integer actual = sut.size(fixture);
    

    //verify outcome
    assertThat(actual).isEqualTo(1234);

    

  }

}
