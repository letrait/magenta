package org.magenta.core.data.supplier;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.Test;
import org.magenta.DataSupplier;

import com.google.common.reflect.TypeToken;

public class FilteredDataSupplierDecoratorTest {
  
  @Test
  public void testNoFiltering(){
    //setup fixture
    
    DataSupplier<Integer> integers = new StaticDataSupplier<>(Arrays.asList(1,2,3,4), TypeToken.of(Integer.class));
    
    FilteredDataSupplierDecorator<Integer> filtered = new FilteredDataSupplierDecorator<>(integers, integer -> true);
    
    //exercise sut and verify outcome

    assertThat(filtered.isEmpty()).isFalse();
    assertThat(filtered.getSize()).isEqualTo(integers.getSize());
    assertThat(filtered.isConstant()).isEqualTo(integers.isConstant()).isTrue();
    assertThat(filtered.isGenerated()).isEqualTo(integers.isGenerated()).isFalse();
    assertThat(filtered.getType()).isEqualTo(TypeToken.of(Integer.class));
    
    assertThat(filtered).containsExactly(1,2,3,4);
  }
  
  @Test
  public void testWithFiltering(){
    //setup fixture
    
    DataSupplier<Integer> integers = new StaticDataSupplier<>(Arrays.asList(1,2,3,4), TypeToken.of(Integer.class));
    
    FilteredDataSupplierDecorator<Integer> filtered = new FilteredDataSupplierDecorator<>(integers, integer -> integer % 2 == 0);
    
    //exercise sut and verify outcome

    assertThat(filtered.isEmpty()).isFalse();
    assertThat(filtered.getSize()).isEqualTo(2);
    assertThat(filtered.isConstant()).isEqualTo(integers.isConstant()).isTrue();
    assertThat(filtered.isGenerated()).isEqualTo(integers.isGenerated()).isFalse();
    assertThat(filtered.getType()).isEqualTo(TypeToken.of(Integer.class));
    
    assertThat(filtered).containsExactly(2,4);
  }
}
