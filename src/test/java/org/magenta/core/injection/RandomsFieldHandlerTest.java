package org.magenta.core.injection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Random;

import org.junit.Test;
import org.magenta.Fixture;
import org.magenta.annotations.InjectFluentRandom;
import org.magenta.random.FluentRandom;
import org.mockito.Mockito;

import com.google.common.base.Suppliers;
import com.google.common.collect.Range;


public class RandomsFieldHandlerTest  {


  @InjectFluentRandom
  private FluentRandom randomBuilder;


  @Test
  public void shouldInjectTheRandoms() throws NoSuchFieldException, SecurityException{

    //setup fixtures
    int CANNED_RESPONSE = 10;
    Random random = mock(Random.class);
    FluentRandom expected = FluentRandom.get(random);
    when(random.nextInt(Mockito.any(Integer.class))).thenReturn(CANNED_RESPONSE);

    Fixture mock = mock(Fixture.class);
    when(mock.getRandomizer()).thenReturn(expected);


    Field f = this.getClass().getDeclaredField("randomBuilder");
    FluentRandomFieldHandler sut = new FluentRandomFieldHandler();

    //exercise sut
    boolean handled = sut.handle(f, this, Suppliers.ofInstance(mock));

    //verify outcome
    assertThat(handled).overridingErrorMessage("The field 'randomBuilder' of this test class was expected to be handled").isTrue();
    assertThat(randomBuilder).overridingErrorMessage("No RandomBuilderImplinjected into field 'randomBuilder'").isNotNull();

    assertThat(randomBuilder.integers().any(Range.closed(0, 20))).isEqualTo(10);
  }


}
