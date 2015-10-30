package org.magenta.core.data.supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Test;
import org.magenta.DataSupplier;
import org.magenta.core.data.supplier.ResizedDataSupplierDecorator;
import org.magenta.core.data.supplier.StaticDataSupplier;

import com.google.common.reflect.TypeToken;

public class ResizedDataSupplierDecoratorTest {


  @Test
  public void test_resize_should_affect_iteration() {
    // setup fixtures
    Integer[] numbers = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
    DataSupplier<Integer> delegate = new StaticDataSupplier<Integer>(Arrays.asList(numbers), TypeToken.of(Integer.class));

    // exercis sut

    ResizedDataSupplierDecorator<Integer> sut = new ResizedDataSupplierDecorator<Integer>(delegate, 3);

    // verify outcome
    assertThat(sut.getSize()).isEqualTo(3);
    assertThat(sut).containsExactly(1, 2, 3);

  }
}
