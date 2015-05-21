package org.magenta.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.magenta.DataKey;
import org.magenta.DataSet;
import org.magenta.FixtureFactory;
import org.magenta.core.DataSetSupplier;
import org.mockito.Mockito;

import com.google.common.base.Suppliers;

public class DatasetSupplierTest {

  @Test
  public void testGet(){
    //setup fixture
    DataSet<String> expected = mock(DataSet.class);
    FixtureFactory fixture = mock(FixtureFactory.class);

    when(fixture.dataset(Mockito.any(DataKey.class))).thenReturn(expected);

    DataSetSupplier<String> sut = DataSetSupplier.forKey(DataKey.of(String.class), Suppliers.ofInstance(fixture));

    //exercise sut
    DataSet<String> actual = sut.get();

    //verify outcome

    verify(fixture).dataset(DataKey.of(String.class));
    assertThat(actual).isSameAs(expected);
  }
}
