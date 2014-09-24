package org.magenta.core.injection;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Random;

import org.junit.Test;
import org.magenta.DataDomain;
import org.magenta.annotations.InjectRandomBuilder;
import org.magenta.random.RandomBuilder;
import org.mockito.Mockito;

import com.google.common.base.Suppliers;
import com.google.common.collect.Range;


public class RandomsFieldHandlerTest  {


  @InjectRandomBuilder
  private RandomBuilder randomBuilder;


  @Test
  public void shouldInjectTheRandoms() throws NoSuchFieldException, SecurityException{

    //setup fixtures
    int CANNED_RESPONSE = 10;
    Random random = mock(Random.class);
    RandomBuilder expected = RandomBuilder.PROVIDER.get(random);
    when(random.nextInt(Mockito.any(Integer.class))).thenReturn(CANNED_RESPONSE);

    DataDomain mock = mock(DataDomain.class);
    when(mock.getRandomizer()).thenReturn(expected);


    Field f = this.getClass().getDeclaredField("randomBuilder");
    RandomBuilderFieldHandler sut = new RandomBuilderFieldHandler();

    //exercise sut
    boolean handled = sut.handle(f, this, Suppliers.ofInstance(mock));

    //verify outcome
    assertThat(handled).overridingErrorMessage("The field 'randomBuilder' of this test class was expected to be handled").isTrue();
    assertThat(randomBuilder).overridingErrorMessage("No RandomBuilder injected into field 'randomBuilder'").isNotNull();

    assertThat(randomBuilder.integers().any(Range.closed(0, 20))).isEqualTo(10);
  }


}
