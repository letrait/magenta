package org.magenta.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Map;

import org.junit.Test;
import org.magenta.DataKey;
import org.magenta.DataSet;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class DataSetFunctionRegistryTest {

  @Test
  public void testRegister(){
    //setup fixture
    DataSetFunctionRegistry sut = new DataSetFunctionRegistry();
    DataKey<String> key = DataKey.of(String.class);
    DataSet<String> dataset = mock(DataSet.class);
    Function<Object,DataSet<String>> function = Functions.constant(dataset);


    //exercise sut
    sut.register(key,function);

    //verify outcome
    assertThat(sut.get(key)).isSameAs(function);
  }

  @Test(expected = NullPointerException.class)
  public void testRegister_null_should_not_accept_null_key(){
    //setup fixture
    DataSetFunctionRegistry sut = new DataSetFunctionRegistry();

    DataSet<String> dataset = mock(DataSet.class);
    Function<Object,DataSet<String>> function = Functions.constant(dataset);


    //exercise sut
    sut.register(null,function);


  }

  @Test(expected = NullPointerException.class)
  public void testRegister_null_should_not_accept_null_function(){
    //setup fixture
    DataSetFunctionRegistry sut = new DataSetFunctionRegistry();

    DataKey<String> key = DataKey.of(String.class);


    //exercise sut
    sut.register(key,null);


  }

  @Test
  public void testKeys(){

    //setup fixture
    DataSetFunctionRegistry sut = new DataSetFunctionRegistry();
    DataKey<String> key1 = DataKey.of(String.class);
    DataKey<Integer> key2 = DataKey.of(Integer.class);
    DataKey<Double> key3 = DataKey.of(Double.class);
    DataSet<String> dataset1 = mock(DataSet.class);
    DataSet<Integer> dataset2 = mock(DataSet.class);
    DataSet<Double> dataset3 = mock(DataSet.class);


    Function<Object,DataSet<String>> function1 = Functions.constant(dataset1);
    Function<Object,DataSet<Integer>> function2 = Functions.constant(dataset2);
    Function<Object,DataSet<Double>> function3 = Functions.constant(dataset3);

    //exercise sut
    sut.register(key1,function1);
    sut.register(key2,function2);
    sut.register(key3,function3);

    //verify outcom
    assertThat(sut.keys()).containsExactly(key1,key2, key3);

  }

  @Test
  public void testUnion(){

    Map<Integer,String> integers1 = Maps.newLinkedHashMap();
    integers1.put(1, "1");
    integers1.put(2, "2");
    integers1.put(3, "3");

    Map<Integer, String> integers2 = Maps.newLinkedHashMap();

    integers2.put(4, "4");
    integers2.put(5, "5");
    integers2.put(6, "6");


    assertThat(Sets.union(integers1.keySet(),integers2.keySet())).containsExactly(1,2,3,4,5,6);
  }

}
