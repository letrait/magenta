package org.magenta.core.data.supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.magenta.Fixture;
import org.magenta.core.context.ThreadLocalFixtureContext;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.base.Supplier;

@RunWith(MockitoJUnitRunner.class)
public class ContextualizedSupplierFunctionAdapterTest {

  @Mock
  private Supplier<Integer> delegate;

  @Mock
  private Fixture fixture;

  @Mock
  private ThreadLocalFixtureContext threadLocal;

  @Test
  public void test_fixture_is_set_to_threadlocal_before_delegating(){

    //setup fixtures
    when(delegate.get()).thenReturn(1);

    ContextualizedSupplierFunctionAdapter<Integer> sut = new ContextualizedSupplierFunctionAdapter<Integer>(delegate, threadLocal);

    //exercise sut
    Integer actual = sut.apply(fixture);

    //verify outcome
    assertThat(actual).isEqualTo(1);

    InOrder order = Mockito.inOrder(threadLocal, delegate);

    order.verify(threadLocal).set(fixture);
    order.verify(delegate).get();
    order.verify(threadLocal).clear();

  }

  @Test
  public void test_fixture_is_removed_from_threadlocal_even_if_there_is_an_exception_during_delegation(){

    //setup fixtures
    when(delegate.get()).thenThrow(new RuntimeException("test exception"));

    ContextualizedSupplierFunctionAdapter<Integer> sut = new ContextualizedSupplierFunctionAdapter<Integer>(delegate, threadLocal);

    //exercise sut

    try{
      sut.apply(fixture);
      fail("no exception thrown");
    }catch(Exception re){

    }


    //verify outcome


    InOrder order = Mockito.inOrder(threadLocal, delegate);

    order.verify(threadLocal).set(fixture);
    order.verify(delegate).get();
    order.verify(threadLocal).clear();

  }



  @Test
  public void test_thread_local_is_not_set_if_already_set(){

    //setup fixtures
    when(threadLocal.isSet()).thenReturn(true);
    when(delegate.get()).thenReturn(1);

    ContextualizedSupplierFunctionAdapter<Integer> sut = new ContextualizedSupplierFunctionAdapter<Integer>(delegate, threadLocal);

    //exercise sut
    Integer actual = sut.apply(fixture);

    //verify outcome
    assertThat(actual).isEqualTo(1);

    verify(threadLocal,never()).set(fixture);
    verify(threadLocal,never()).clear();

  }
}
