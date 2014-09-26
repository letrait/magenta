package org.magenta.core.injection;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.junit.Test;
import org.magenta.Fixture;
import org.mockito.InOrder;
import org.mockito.Mockito;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

public class FieldInjectionProcessorTest {

  private String aField1;
  private String aField2;

  @Test
  public void test() throws NoSuchFieldException, SecurityException{

    //setup fixture
    Fixture fixture = mock(Fixture.class);
    Supplier<Fixture> currentFixture = Suppliers.ofInstance(fixture);

    FieldInjectionHandler handler1 = mock(FieldInjectionHandler.class);
    FieldInjectionHandler handler2 = mock(FieldInjectionHandler.class);

    Injector sut = new FieldInjectionChainProcessor(currentFixture, HiearchicalFieldsFinder.SINGLETON, Arrays.asList(handler1, handler2));

    Field f1 = this.getClass().getDeclaredField("aField1");
    Field f2 = this.getClass().getDeclaredField("aField2");

    when(handler1.handle(f1, this, currentFixture)).thenReturn(false);
    when(handler2.handle(f1, this, currentFixture)).thenReturn(true);
    when(handler1.handle(f2, this, currentFixture)).thenReturn(true);
    when(handler2.handle(f2, this, currentFixture)).thenReturn(true);

    //exercise sut
    sut.inject(this);

    //verify outcome
    InOrder inOrder = Mockito.inOrder(handler1,handler2);

    inOrder.verify(handler1).handle(f1, this, currentFixture);
    inOrder.verify(handler2).handle(f1, this, currentFixture);

    inOrder.verify(handler1).handle(f2, this, currentFixture);
    inOrder.verify(handler2,Mockito.never()).handle(f2, this, currentFixture);
  }
}
