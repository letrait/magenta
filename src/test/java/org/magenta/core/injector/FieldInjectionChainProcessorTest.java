package org.magenta.core.injector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Map;

import org.junit.Test;
import org.magenta.Fixture;
import org.magenta.core.Injector;
import org.magenta.core.injector.FieldInjectionChainProcessor;
import org.magenta.core.injector.FieldInjectionHandler;
import org.mockito.InOrder;
import org.mockito.Mockito;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.inject.internal.ImmutableMap;

public class FieldInjectionChainProcessorTest {

  @Test
  public void test_each_handler_are_called() throws NoSuchFieldException, SecurityException{

    //setup fixture
    Map<Injector.Key<?>,Object> values1 = ImmutableMap.of(Injector.Key.NUMBER_OF_COMBINATION_FUNCTION,10);
    Map<Injector.Key<?>,Object> values2 = ImmutableMap.of(Injector.Key.NUMBER_OF_COMBINATION_FUNCTION,20);
    
    Fixture fixture = mock(Fixture.class);
    Supplier<Fixture> currentFixture = Suppliers.ofInstance(fixture);

    FieldInjectionHandler handler1 = mock(FieldInjectionHandler.class);
    FieldInjectionHandler handler2 = mock(FieldInjectionHandler.class);
    
    when(handler1.injectInto(Mockito.any(Object.class), Mockito.any(Supplier.class))).thenReturn(values1);
    when(handler1.injectInto(Mockito.any(Object.class), Mockito.any(Supplier.class))).thenReturn(values2);

    Injector sut = new FieldInjectionChainProcessor(
        Arrays.asList(handler1, handler2),
            currentFixture);

    //exercise sut
    Map<Injector.Key<?>,Object> actual = sut.inject(this);

    //verify outcome
    assertThat(actual).containsEntry(Injector.Key.NUMBER_OF_COMBINATION_FUNCTION,20);
    
    InOrder inOrder = Mockito.inOrder(handler1,handler2);

    inOrder.verify(handler1).injectInto(this, currentFixture);
    inOrder.verify(handler2).injectInto(this, currentFixture);

  }
}
