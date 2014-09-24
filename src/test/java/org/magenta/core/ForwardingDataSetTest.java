package org.magenta.core;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.magenta.DataSet;
import org.mockito.Mockito;

import com.google.common.base.Suppliers;

public class ForwardingDataSetTest {

  @Test
  public void testDelegation() {

    // setup fixtures
    DataSet<String> delegate = Mockito.mock(DataSet.class);

    ForwardingDataSet<String> sut = new ForwardingDataSet<String>(Suppliers.ofInstance(delegate));

    // exercise sut
    sut.any();
    sut.equals(null);
    sut.filter(null);
    sut.get();
    sut.hashCode();
    sut.link(null);
    sut.list();
    sut.list(0);
    sut.randomList();
    sut.randomList(0);
    sut.reverseLink(null, null);
    sut.set();
    sut.set(0);
    sut.subset(0);
    sut.transform(null, null);

    // verify outcome
    verify(delegate).any();
    verify(delegate).filter(null);
    verify(delegate).get();
    verify(delegate).link(null);
    verify(delegate).list();
    verify(delegate).list(0);
    verify(delegate).randomList();
    verify(delegate).randomList(0);
    verify(delegate).reverseLink(null, null);
    verify(delegate).set();
    verify(delegate).set(0);
    verify(delegate).subset(0);
    verify(delegate).transform(null, null);

  }

  @Test
  public void testEquals() {
    // setup fixtures
    DataSet<String> delegate = Fixtures.createAnonymousDataSet(10);
    DataSet<String> sut = new ForwardingDataSet<String>(Suppliers.ofInstance(delegate));
    DataSet<String> same = new ForwardingDataSet<String>(Suppliers.ofInstance(delegate));

    // exercise and verify outcome
    assertThat(delegate).isEqualTo(sut)
        .isEqualTo(same);
    assertThat(sut).isEqualTo(delegate)
        .isEqualTo(same);

  }
}
