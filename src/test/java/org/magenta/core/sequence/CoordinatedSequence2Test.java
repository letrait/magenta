package org.magenta.core.sequence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.magenta.DataSet;
import org.magenta.DataSupplier;
import org.magenta.Sequence;
import org.magenta.core.DataSetImpl;
import org.magenta.core.data.supplier.StaticDataSupplier;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;

public class CoordinatedSequence2Test {

  @Test
  public void testReadingFromOneSequence(){

    //setup fixture
    Integer[] expected = new Integer[]{1,2,3,4,5};
    DataSet<Integer> integers = createDataSetFrom(expected);
    Sequence<Integer> uniqueSequence = createUniqueSequence(integers);

    //exercise sut
    List<Integer> actual = readFromSequence(uniqueSequence,5);

    //verify outcome
    assertThat(actual).containsExactly(expected);

  }

  @Test
  public void testReadingFromTwoCoordinatedSequences(){

    //setup fixture

    DataSet<Integer> set1 = createDataSetFrom(1,2);
    DataSet<Integer> set2 = createDataSetFrom(10,20,30);
    List<Sequence<?>> sequences = createSequences(set1,set2);

    //exercise sut
    List<Integer> actual = readFromSequences(sequences,6);

    //verify outcome
    assertThat(actual).as(actual.toString()).containsExactly(1,10,2,10,1,20,2,20,1,30,2,30);

  }

  @Test
  public void testReadingFromTwoCoordinatedSequencesInDifferentOrder(){

    //setup fixture

    DataSet<Integer> set1 = createDataSetFrom(1,2);
    DataSet<Integer> set2 = createDataSetFrom(10,20,30);
    List<Sequence<?>> sequences = createSequences(set1,set2);

    //exercise sut
    List<Integer> actual = readFromSequences(Lists.reverse(sequences),6);

    //verify outcome

    assertThat(actual).as(actual.toString()).containsExactly(10,1,10,2,20,1,20,2,30,1,30,2);

  }

  @Test
  public void testReadingFromThreeCoordinatedSequences() {

    // setup fixture

    DataSet<Integer> set1 = createDataSetFrom(1, 2);
    DataSet<Integer> set2 = createDataSetFrom(10, 20, 30);
    DataSet<Integer> set3 = createDataSetFrom(100, 200, 300, 400);
    List<Sequence<?>> sequences = createSequences(set1, set2, set3);

    // exercise sut
    List<Integer> actual = readFromSequences(sequences, 24);

    // verify outcome
    assertThat(actual).as(actual.toString()).containsExactly(1, 10, 100, 2, 10, 100, 1, 20, 100, 2, 20, 100, 1, 30, 100, 2, 30, 100, 1, 10, 200, 2,
        10, 200, 1, 20, 200, 2, 20, 200, 1, 30, 200, 2, 30, 200, 1, 10, 300, 2, 10, 300, 1, 20, 300, 2, 20, 300, 1, 30, 300, 2, 30, 300, 1, 10, 400,
        2, 10, 400, 1, 20, 400, 2, 20, 400, 1, 30, 400, 2, 30, 400);

  }





  @Test
  public void testReadingFromOneSequenceMultipleLoops(){

    //setup fixture
    DataSet<Integer> integers = createDataSetFrom(1,2);
    Sequence<Integer> uniqueSequence = createUniqueSequence(integers);

    //exercise sut
    List<Integer> actual = readFromSequence(uniqueSequence,5);

    //verify outcome
    assertThat(actual).containsExactly(1,2,1,2,1);

  }


  private <D> List<D> readFromSequence(Sequence<D> sequence, int numberOfItems) {
    List<D> values = Lists.newArrayList();

    for(int i = 0; i<numberOfItems;i++){
      values.add(sequence.next());
    }
    return values;
  }

  private List readFromSequences(List<Sequence<?>> sequences, int numberOfItems) {
    List values = Lists.newArrayList();

    for(int i = 0; i<numberOfItems;i++){
      for(Sequence<?> s:sequences){
        values.add(s.next());
      }
    }
    return values;
  }

  private <D> Sequence<D> createUniqueSequence(DataSet<D> dataset) {
    return new CoordinatedSequence<D>(dataset, false, new SequenceCoordinator());
  }

  private <D> Sequence<D> createSequence(DataSet<D> d, SequenceCoordinator coordinator) {
    return new CoordinatedSequence<D>(d, false, coordinator);
  }

  private List<Sequence<?>> createSequences(DataSet<?>...sets) {
    List<Sequence<?>> sequences = Lists.newArrayList();

    SequenceCoordinator coordinator = new SequenceCoordinator();
    for(DataSet<?> d:sets){
      sequences.add(createSequence(d,coordinator));
    }
    return sequences;
  }



  private <D> DataSetImpl<D> createDataSetFrom(D...staticData) {
    D proto = staticData[0];
    TypeToken<D> expectedType = TypeToken.of((Class)proto.getClass());

    DataSupplier<D> supplier = new StaticDataSupplier<D>(Arrays.asList(staticData), expectedType);
    DataSetImpl<D> sut = createDataSetFrom(supplier);
    return sut;
  }


  private <D> DataSetImpl<D> createDataSetFrom(DataSupplier<D> supplier) {
    return new DataSetImpl<D>(supplier, true);
  }
}
