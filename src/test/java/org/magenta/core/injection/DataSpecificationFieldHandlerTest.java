package org.magenta.core.injection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.junit.Test;
import org.magenta.DataSpecification;
import org.magenta.Fixture;
import org.magenta.SimpleDataSpecification;
import org.magenta.annotations.InjectDataSpecification;


public class DataSpecificationFieldHandlerTest  {


  @InjectDataSpecification
  private DataSpecification dataSpecification;


  @Test
  public void shouldInjectTheDataSpecification() throws NoSuchFieldException, SecurityException{

    //setup fixtures
    int EXPECTED_NUMBER_OF_ITEMS = 1234;
    DataSpecification expected = SimpleDataSpecification.create().defaultNumberOfItems(EXPECTED_NUMBER_OF_ITEMS);


    Fixture mock = mock(Fixture.class);
    when(mock.getSpecification()).thenReturn(expected);

    Field f = this.getClass().getDeclaredField("dataSpecification");
    DataSpecificationFieldHandler sut = new DataSpecificationFieldHandler();

    //exercise sut
    boolean handled = sut.handle(f, this,()->mock);

    //verify outcome
    assertThat(handled).overridingErrorMessage("The field 'dataSpecification' of this test class was expected to be handled").isTrue();
    assertThat(dataSpecification).overridingErrorMessage("No DataSpecification injected into field 'dataSpecification'").isNotNull();

    assertThat(dataSpecification.getDefaultNumberOfItems()).isEqualTo(EXPECTED_NUMBER_OF_ITEMS);
  }


}
