package org.magenta.core.sequence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.magenta.core.sequence.CoordinatedSequence;
import org.magenta.core.sequence.SequenceCoordinator;

import com.google.common.collect.Lists;

public class SequenceCoordinatorTest {

  @Test
  public void coordination_of_a_single_sequence(){
    //setup fixture

    CoordinatedSequence<?> sequence = createSequenceWithSize(3);

    SequenceCoordinator sut =new SequenceCoordinator();

    //exercise sut
    sut.coordinate(sequence);

    List<Integer> actual = readIndexFor(sut, 7,sequence);

    //verify outcome
    assertThat(actual).containsExactly(0,1,2,0,1,2,0);
    assertThat(sut.numberOfCombination()).isEqualTo(3);

  }

  @Test
  public void coordination_of_a_double_sequence_with_multiple_read(){
    //setup fixture

    CoordinatedSequence<?> sequence1 = createSequenceWithSize(2);
    CoordinatedSequence<?> sequence2 = createSequenceWithSize(2);

    SequenceCoordinator sut =new SequenceCoordinator();

    //exercise sut
    sut.coordinate(sequence1);
    sut.coordinate(sequence2);

    //verify outcome
    
    assertThat(sut.numberOfCombination()).isEqualTo(2 * 2);
    
    assertThat(sut.getIndexFor(sequence1)).isEqualTo(0);
    assertThat(sut.getIndexFor(sequence1)).isEqualTo(0);
    assertThat(sut.getIndexFor(sequence1)).isEqualTo(0);
    assertThat(sut.getIndexFor(sequence2)).isEqualTo(0);
    assertThat(sut.getIndexFor(sequence2)).isEqualTo(0);
    assertThat(sut.getIndexFor(sequence2)).isEqualTo(0);
    assertThat(sut.getIndexFor(sequence1)).isEqualTo(1);
    assertThat(sut.getIndexFor(sequence1)).isEqualTo(0);
    assertThat(sut.getIndexFor(sequence1)).isEqualTo(0);
    assertThat(sut.getIndexFor(sequence2)).isEqualTo(1);
    assertThat(sut.getIndexFor(sequence2)).isEqualTo(1);
    assertThat(sut.getIndexFor(sequence2)).isEqualTo(1);
    assertThat(sut.getIndexFor(sequence1)).isEqualTo(1);
    assertThat(sut.getIndexFor(sequence2)).isEqualTo(0);




  }

  @Test
  public void coordination_of_triple_sequences(){
    //setup fixture

    CoordinatedSequence<?> sequence1 = createSequenceWithSize(2);
    CoordinatedSequence<?> sequence2 = createSequenceWithSize(3);
    CoordinatedSequence<?> sequence3 = createSequenceWithSize(1);

    SequenceCoordinator sut =new SequenceCoordinator();

    //exercise sut
    sut.coordinate(sequence1);
    sut.coordinate(sequence2);
    sut.coordinate(sequence3);

    List<Integer> actual = readIndexFor(sut,6,sequence1,sequence2,sequence3);

    //verify outcome
    assertThat(sut.numberOfCombination()).isEqualTo(2 * 3 * 1);
    assertThat(actual).containsExactly(0,0,0,1,0,0,0,1,0,1,1,0,0,2,0,1,2,0);

  }

  @Test(expected=IllegalArgumentException.class)
  public void coordination_of_a_non_existing_sequence(){
    //setup fixture

    CoordinatedSequence<?> sequence = createSequenceWithSize(3);

    SequenceCoordinator sut =new SequenceCoordinator();

    //exercise sut

    List<Integer> actual = readIndexFor(sut, 7,sequence);

    //verify outcome
    assertThat(actual).containsExactly(0,1,2,0,1,2,0);

  }

  private List<Integer> readIndexFor(  SequenceCoordinator sut , int numberOfLoops, CoordinatedSequence<?>...sequences) {
    List<Integer> indexes = Lists.newArrayList();

    for(int i = 0;i<numberOfLoops;i++){
      for(CoordinatedSequence<?> c:sequences){
        indexes.add(sut.getIndexFor(c));
      }
    }

    return indexes;
  }

  private CoordinatedSequence<?> createSequenceWithSize(int size) {
    CoordinatedSequence<?> sequence =mock(CoordinatedSequence.class);
    when(sequence.size()).thenReturn(size);
    return sequence;
  }

}
