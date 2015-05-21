package org.magenta.core.data.supplier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.magenta.DataSupplier;
import org.magenta.core.data.supplier.ForwardingDataSupplier;
import org.mockito.Mockito;

public class ForwardingDataSupplierTest {

  @Test
  public void testForwarding(){

    //setup fixtures
    int expectedIndex = 12;
    DataSupplier<Integer> delegate = mock(DataSupplier.class);

    ForwardingDataSupplier<Integer> sut = new ForwardingDataSupplier<Integer>(delegate);

    //exercise sut
    sut.get(expectedIndex);
    sut.getMaximumSize();
    sut.getSize();
    sut.getType();
    sut.isConstant();
    sut.isEmpty();
    sut.isGenerated();
    sut.iterator();


    //verify outcome
    verify(delegate).get(expectedIndex);
    verify(delegate).getMaximumSize();
    verify(delegate).getSize();
    verify(delegate).getType();
    verify(delegate).isConstant();
    verify(delegate).isEmpty();
    verify(delegate).isGenerated();
    verify(delegate).iterator();

    Mockito.verifyNoMoreInteractions(delegate);

  }
}
