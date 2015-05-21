package org.magenta.core.sequence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.Test;
import org.magenta.DataSet;
import org.magenta.DataSupplier;
import org.magenta.core.DataSetImpl;
import org.magenta.core.data.supplier.StaticDataSupplier;
import org.magenta.core.sequence.OrderedSequence;
import org.magenta.random.FluentRandom;

import com.google.common.base.Suppliers;
import com.google.common.reflect.TypeToken;

public class OrderedSequenceTest {

  @Test
  public void test(){

    //setup fixture
    DataSet<Integer> dataset = new DataSetImpl<Integer>(staticData(1,2,3,4), Suppliers.ofInstance(FluentRandom.singleton()));
    OrderedSequence<Integer> sut = OrderedSequence.from(dataset);

    //exercise sut / verify outcome
    assertThat(sut.get()).isEqualTo(1);
    assertThat(sut.get()).isEqualTo(2);
    assertThat(sut.get()).isEqualTo(3);
    assertThat(sut.get()).isEqualTo(4);
    assertThat(sut.get()).isEqualTo(1);
    assertThat(sut.get()).isEqualTo(2);
    assertThat(sut.get()).isEqualTo(3);
    assertThat(sut.get()).isEqualTo(4);

  }

  private DataSupplier<Integer> staticData(Integer...values) {
    return new StaticDataSupplier<Integer>(Arrays.asList(values), TypeToken.of(Integer.class));
  }
}
