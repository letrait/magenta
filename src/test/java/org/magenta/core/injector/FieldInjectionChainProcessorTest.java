package org.magenta.core.injector;

import static org.mockito.Mockito.mock;

import java.util.Arrays;

import org.junit.Test;
import org.magenta.Fixture;
import org.magenta.core.Injector;
import org.magenta.core.injector.FieldInjectionChainProcessor;
import org.magenta.core.injector.FieldInjectionHandler;
import org.mockito.InOrder;
import org.mockito.Mockito;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

public class FieldInjectionChainProcessorTest {

  @Test
  public void test_each_handler_are_called() throws NoSuchFieldException, SecurityException{

    //setup fixture
    Fixture fixture = mock(Fixture.class);
    Supplier<Fixture> currentFixture = Suppliers.ofInstance(fixture);

    FieldInjectionHandler handler1 = mock(FieldInjectionHandler.class);
    FieldInjectionHandler handler2 = mock(FieldInjectionHandler.class);

    Injector sut = new FieldInjectionChainProcessor(
        Arrays.asList(handler1, handler2),
            currentFixture);

    //exercise sut
    sut.inject(this);

    //verify outcome
    InOrder inOrder = Mockito.inOrder(handler1,handler2);

    inOrder.verify(handler1).injectInto(this, currentFixture);
    inOrder.verify(handler2).injectInto(this, currentFixture);

  }
}
