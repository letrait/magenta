package org.magenta.core.data.supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;

public class LazyGeneratedDataSupplierTest {

  @Test
  public void testConstruction() {

    final int SIZE = 3;
    Supplier<String> generator = mock(Supplier.class);
    Supplier<Integer> sizeOf = Suppliers.ofInstance(SIZE);

    when(generator.get()).thenReturn("a","b","c");

    LazyGeneratedDataSupplier<String> sut = new LazyGeneratedDataSupplier<>(TypeToken.of(String.class), generator, sizeOf);

    assertThat(sut.getSize()).isEqualTo(SIZE);
    assertThat(sut.getType()).isEqualTo(TypeToken.of(String.class));
    assertThat(sut.isConstant()).isEqualTo(true);
    assertThat(sut.isGenerated()).isEqualTo(true);
    assertThat(sut.iterator()).containsExactly("a","b","c");

  }

  @Test
  public void testGetAtIndex() {

    // setup fixtures
    String[] NUMBER_ARRAY = new String[] { "10","9","8" };

    final int SIZE = NUMBER_ARRAY.length;
    Supplier<String> generator = mock(Supplier.class);

    when(generator.get()).thenReturn("10", "9", "8");
    Supplier<Integer> sizeOf = Suppliers.ofInstance(SIZE);

    LazyGeneratedDataSupplier<String> sut = new LazyGeneratedDataSupplier<>(TypeToken.of(String.class), generator, sizeOf);

    // exercise sut
    for (int i = 0; i < NUMBER_ARRAY.length; i++) {
      assertThat(sut.get(i)).isEqualTo(NUMBER_ARRAY[i]);
    }

  }

  @Test
  public void testGetAtIndexRandom() {

    // setup fixtures
    Integer[] NUMBER_ARRAY = new Integer[] { 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 };

    final int SIZE = NUMBER_ARRAY.length;
    Supplier<Integer> generator = mock(Supplier.class);

    when(generator.get()).thenReturn(10, 9, 8, 7, 6, 5, 4, 3, 2, 1);
    Supplier<Integer> sizeOf = Suppliers.ofInstance(SIZE);

    LazyGeneratedDataSupplier<Integer> sut = new LazyGeneratedDataSupplier<>(TypeToken.of(Integer.class), generator, sizeOf);

    assertThat(sut.iterator()).containsExactly(NUMBER_ARRAY);

    List<Integer> shuffledIndexes = shuffleIndexes(SIZE);

    List<Integer> expectedRandomOrder = readNumberArrayInTheSpecifiedOrder(NUMBER_ARRAY, shuffledIndexes);

    // exercise sut
    List<Integer> actualRandomOrder = readDataSupplierInSpecfiedOrder(sut, shuffledIndexes);

    //verify outcome

    assertThat(actualRandomOrder).isEqualTo(expectedRandomOrder);

  }

  @Test
  public void testGetAtIndex_when_generator_return_null() {

    // setup fixtures
    Integer[] NUMBER_ARRAY = new Integer[] { null, 1234 };

    final int SIZE = NUMBER_ARRAY.length;
    Supplier<Integer> generator = mock(Supplier.class);

    when(generator.get()).thenReturn(null,1234);
    Supplier<Integer> sizeOf = Suppliers.ofInstance(SIZE);

    LazyGeneratedDataSupplier<Integer> sut = new LazyGeneratedDataSupplier<>(TypeToken.of(Integer.class), generator, sizeOf);

    // exercise sut and verify outcome
    assertThat(sut.iterator()).containsExactly(NUMBER_ARRAY);
    assertThat(sut.iterator()).containsExactly(NUMBER_ARRAY);
    assertThat(sut.get(0)).isEqualTo(NUMBER_ARRAY[0]);
    assertThat(sut.get(1)).isEqualTo(NUMBER_ARRAY[1]);

  }

  @Test
  public void testGetAtIndex_when_index_is_greater_than_the_specified_size_should_be_possible(){

    // setup fixtures
    final int SIZE = 1;
    Supplier<String> generator = mock(Supplier.class);

    when(generator.get()).thenReturn("a","b","c");
    Supplier<Integer> sizeOf = Suppliers.ofInstance(SIZE);

    LazyGeneratedDataSupplier<String> sut = new LazyGeneratedDataSupplier<String>(TypeToken.of(String.class), generator, sizeOf);

    // exercise sut and verify outcome
    assertThat(sut.iterator()).containsExactly("a");
    assertThat(sut.get(1)).isEqualTo("b");
    assertThat(sut.get(2)).isEqualTo("c");

  }

  private List<Integer> readNumberArrayInTheSpecifiedOrder(Integer[] NUMBER_ARRAY, List<Integer> shuffledIndexes) {
    List<Integer> expectedRandomOrder = Lists.newArrayList();
    for (Integer index : shuffledIndexes) {
      expectedRandomOrder.add(NUMBER_ARRAY[index]);
    }
    return expectedRandomOrder;
  }

  private List<Integer> readDataSupplierInSpecfiedOrder(LazyGeneratedDataSupplier<Integer> sut, List<Integer> shuffledIndexes) {
    List<Integer> actualRandomOrder = Lists.newArrayList();
    for (Integer index : shuffledIndexes) {
      actualRandomOrder.add(sut.get(index));
    }
    return actualRandomOrder;
  }

  private List<Integer> shuffleIndexes(int size) {
    List<Integer> indexes = Lists.newArrayList();

    for (int i = 0; i < size; i++) {
      indexes.add(i);
    }

    Collections.shuffle(indexes);
    return indexes;
  }

}
