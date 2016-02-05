package org.magenta.core.data.supplier;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.Test;

import com.google.common.reflect.TypeToken;

public class StaticDataSupplierTest {

  @Test
  public void testSimpleIntegerArrays(){

    StaticDataSupplier<Integer> numbers = numbers(1,2,3,4,5,6);

    assertThat(numbers.iterator()).containsExactly(1,2,3,4,5,6);
    assertThat(numbers.getSize()).isEqualTo(6);
    assertThat(numbers.isEmpty()).isFalse();
    assertThat(numbers.getType()).isEqualTo(TypeToken.of(Integer.class));

    assertThat(numbers.isConstant()).isTrue();
    assertThat(numbers.isGenerated()).isFalse();
  }

  @Test
  public void testEmptyData(){

    StaticDataSupplier<Integer> numbers = numbers();

    assertThat(numbers.iterator()).isEmpty();
    assertThat(numbers.getSize()).isEqualTo(0);
    assertThat(numbers.isEmpty()).isTrue();
    assertThat(numbers.getType()).isEqualTo(TypeToken.of(Integer.class));

    assertThat(numbers.isConstant()).isTrue();
    assertThat(numbers.isGenerated()).isFalse();
  }

  @Test
  public void testGetAtIndex(){

    Integer[] numberArray = new Integer[]{10,9,8,7,6,5,4,3,2,1};

    StaticDataSupplier<Integer> numbers = numbers(numberArray);

    for(int i = 0; i<numberArray.length;i++){
      assertThat(numbers.get(i)).isEqualTo(numberArray[i]);
    }
  }

  private StaticDataSupplier<Integer> numbers(Integer...numbers) {
    return new StaticDataSupplier<Integer>(Arrays.asList(numbers), TypeToken.of(Integer.class));
  }
}
