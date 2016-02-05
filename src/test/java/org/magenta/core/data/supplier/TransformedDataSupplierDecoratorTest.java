package org.magenta.core.data.supplier;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.magenta.DataSupplier;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;


public class TransformedDataSupplierDecoratorTest {

  @Test
  public void testDelegation(){
    
    //setup fixture
    DataSupplier<Integer> integers = new StaticDataSupplier<>(Arrays.asList(1,2,3,4), TypeToken.of(Integer.class));
    
    TransformedDataSupplierDecorator<Integer, Integer> toStrings = new TransformedDataSupplierDecorator<Integer,Integer>(integers, integer -> integer,  TypeToken.of(Integer.class));

    //verify outcome
    assertThat(toStrings.isEmpty()).isFalse();
    assertThat(toStrings.getSize()).isEqualTo(integers.getSize());
    assertThat(toStrings.isConstant()).isEqualTo(integers.isConstant()).isTrue();
    assertThat(toStrings.isGenerated()).isEqualTo(integers.isGenerated()).isFalse();
    assertThat(toStrings.getType()).isEqualTo(TypeToken.of(Integer.class));
    
  }
  
  @Test
  public void testTransformation(){
    
    //setup fixture
    DataSupplier<Integer> integers = new StaticDataSupplier<>(Arrays.asList(1,2,3,4), TypeToken.of(Integer.class));
    
    TransformedDataSupplierDecorator<Integer, String> toStrings = new TransformedDataSupplierDecorator<Integer,String>(integers, integer -> integer.toString(),  TypeToken.of(String.class));
    
    //exercise sut
    List<String> actual = Lists.newArrayList(toStrings);
    
    //verify outcome
    assertThat(actual).containsExactly("1", "2", "3", "4");
    
  }
}
