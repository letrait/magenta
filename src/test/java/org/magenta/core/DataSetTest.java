package org.magenta.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.magenta.DataSet;
import org.magenta.DataSupplier;
import org.magenta.core.DataSetImpl;
import org.magenta.core.data.supplier.StaticDataSupplier;
import org.magenta.random.FluentRandom;
import org.magenta.random.RandomList;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.base.Suppliers;
import com.google.common.reflect.TypeToken;

@RunWith(MockitoJUnitRunner.class)
public class DataSetTest {

  @Mock()
  private FluentRandom fluentRandom;

  @Test
  public void testBasicState(){

    Integer[] expected = new Integer[]{1,2,3,4,5,6,7,8,9,10};

    DataSetImpl<Integer> sut = createDataSetFrom(expected);

    //verify outcome
    assertThat(sut.getRandomizer()).isEqualTo(fluentRandom);
    assertThat(sut.getSize()).isEqualTo(expected.length);
    assertThat(sut.getMaximumSize()).isEqualTo(expected.length);
    assertThat(sut.getType()).isEqualTo(TypeToken.of(Integer.class));
    assertThat(sut.isConstant()).isTrue();
    assertThat(sut.isGenerated()).isFalse();
    assertThat(sut.isEmpty()).isFalse();
  }

  @Test
  public void testFirst(){
    //setup fixtures
    Integer[] expected = new Integer[]{1,2,3,4,5,6,7,8,9,10};
    DataSetImpl<Integer> sut = createDataSetFrom(expected);

    //exercise sut
    Integer actual = sut.first();

    //verify outcome
    assertThat(actual).isEqualTo(expected[0]);
  }

  @Test
  public void testAny(){
    //setup fixtures
    int expected = 7;
    Integer[] integers = new Integer[]{1,2,3,4,5,6,7,8,9,10};
    DataSetImpl<Integer> sut = createDataSetFrom(integers);
    when(fluentRandom.iterable(sut)).thenReturn(mock(RandomList.class));
    when(fluentRandom.iterable(sut).any()).thenReturn(expected);
    //exercise sut
    Integer actual = sut.any();

    //verify outcome
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void testIterator(){
    //setup fixtures
    Integer[] expected = new Integer[]{1,2,3,4,5,6,7,8,9,10};
    DataSetImpl<Integer> sut = createDataSetFrom(expected);

    //exercise sut
    Iterator<Integer> actual = sut.iterator();

    //verify outcome
    assertThat(actual).containsExactly(expected);
  }

  @Test
  public void testResize(){
    //setup fixtures
    Integer[] origin = new Integer[]{1,2,3,4,5,6,7,8,9,10};
    Integer[] expected = new Integer[]{1,2,3,4};
    DataSetImpl<Integer> parent = createDataSetFrom(origin);

    //exercise sut
    DataSet<Integer> sut = parent.resize(expected.length);
    Iterator<Integer> actual = sut.iterator();

    //verify outcome
    assertThat(sut.getSize()).isEqualTo(expected.length);
    assertThat(sut.getMaximumSize()).isEqualTo(origin.length);
    assertThat(actual).containsExactly(expected);
  }

  @Test
  public void testArray(){
    //setup fixtures
    Integer[] expected = new Integer[]{1,2,3,4,5,6,7,8,9,10};
    DataSetImpl<Integer> sut = createDataSetFrom(expected);

    //exercise sut
    Integer[] actual = sut.array();

    //verify outcome
    assertThat(actual).containsExactly(expected);
  }

  @Test
  public void testResizedArray(){
    //setup fixtures
    Integer[] expected = new Integer[]{1,2,3,4,5,6,7,8,9,10};
    DataSetImpl<Integer> sut = createDataSetFrom(expected);

    //exercise sut
    Integer[] actual = sut.array(4);

    //verify outcome
    assertThat(actual).containsExactly(1,2,3,4);
  }

  @Test
  public void testList(){
    //setup fixtures
    Integer[] expected = new Integer[]{1,2,3,4,5,6,7,8,9,10};
    DataSetImpl<Integer> sut = createDataSetFrom(expected);

    //exercise sut
    List<Integer> actual = sut.list();

    //verify outcome
    assertThat(actual).containsExactly(expected);
  }

  @Test
  public void testResizedList(){
    //setup fixtures
    Integer[] expected = new Integer[]{1,2,3,4,5,6,7,8,9,10};
    DataSetImpl<Integer> sut = createDataSetFrom(expected);

    //exercise sut
    List<Integer> actual = sut.list(3);

    //verify outcome
    assertThat(actual).containsExactly(1,2,3);
  }

  @Test
  public void testSet(){
    //setup fixtures
    Integer[] expected = new Integer[]{1,2,3,4,5,6,7,8,9,10};
    DataSetImpl<Integer> sut = createDataSetFrom(expected);

    //exercise sut
    Set<Integer> actual = sut.set();

    //verify outcome
    assertThat(actual).containsExactly(expected);
  }

  @Test
  public void testResizedSet(){
    //setup fixtures
    Integer[] expected = new Integer[]{1,2,3,4,5,6,7,8,9,10};
    DataSetImpl<Integer> sut = createDataSetFrom(expected);

    //exercise sut
    Set<Integer> actual = sut.set(5);

    //verify outcome
    assertThat(actual).containsExactly(1,2,3,4,5);
  }




  private <D> DataSetImpl<D> createDataSetFrom(D[] staticData) {
    D proto = staticData[0];
    TypeToken<D> expectedType = TypeToken.of((Class)proto.getClass());

    DataSupplier<D> supplier = new StaticDataSupplier<D>(Arrays.asList(staticData), expectedType);
    DataSetImpl<D> sut = createDataSetFrom(supplier);
    return sut;
  }


  private <D> DataSetImpl<D> createDataSetFrom(DataSupplier<D> supplier) {
    return new DataSetImpl<D>(supplier, Suppliers.ofInstance(fluentRandom));
  }
}
