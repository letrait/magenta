package org.magenta.core.data.supplier;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.Test;

import com.google.common.reflect.TypeToken;

public class BoundedDataSupplierIteratorTest {

  @Test
  public void testNormalIteration(){

    //setup fixtures
    Integer[] numbers = new Integer[]{1,2,3,4,5,6,7,8,9,10};
    BoundedDataSupplierIterator<Integer> sut = new BoundedDataSupplierIterator<Integer>(new StaticDataSupplier<>(Arrays.asList(numbers), TypeToken.of(Integer.class)));

    //exercise SUT
    assertThat(sut).containsExactly(numbers);

  }

  @Test
  public void testUnorderedIteration(){

    //setup fixtures
    Integer[] numbers = new Integer[]{1,2,3,4};
    BoundedDataSupplierIterator<Integer> sut = new BoundedDataSupplierIterator<Integer>(new StaticDataSupplier<>(Arrays.asList(numbers), TypeToken.of(Integer.class)));

    //exercise SUT
    assertThat(sut.hasNext()).isTrue();
    assertThat(sut.hasNext()).isTrue();
    assertThat(sut.next()).isEqualTo(1);
    assertThat(sut.next()).isEqualTo(2);
    assertThat(sut.hasNext()).isTrue();
    assertThat(sut.hasNext()).isTrue();
    assertThat(sut.next()).isEqualTo(3);
    assertThat(sut.hasNext()).isTrue();
    assertThat(sut.next()).isEqualTo(4);
    assertThat(sut.hasNext()).isFalse();

  }

  @Test(expected = UnsupportedOperationException.class)
  public void testIteratorIsReadOnly(){

    //setup fixtures
    Integer[] numbers = new Integer[]{1,2,3,4,5,6,7,8,9,10};
    BoundedDataSupplierIterator<Integer> sut = new BoundedDataSupplierIterator<Integer>(new StaticDataSupplier<>(Arrays.asList(numbers), TypeToken.of(Integer.class)));

    //exercise SUT
    sut.remove();

  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testOutOfBoundIteration(){

    //setup fixtures
    Integer[] numbers = new Integer[]{1};
    BoundedDataSupplierIterator<Integer> sut = new BoundedDataSupplierIterator<Integer>(new StaticDataSupplier<>(Arrays.asList(numbers), TypeToken.of(Integer.class)));

    //exercise SUT
    sut.next();
    sut.next();

  }
}
