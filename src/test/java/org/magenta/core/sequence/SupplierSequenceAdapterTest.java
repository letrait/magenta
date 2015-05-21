package org.magenta.core.sequence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.magenta.Sequence;
import org.magenta.core.sequence.SupplierSequenceAdapter;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;

public class SupplierSequenceAdapterTest {

  @Test
  public void adapting_a_supplier_that_is_not_composed_of_other_sequences_should_result_in_a_sequence_with_a_size_of_one(){

    //setup fixture
    final int EXPECTED = 2;
    Supplier<Integer> candidate =  Suppliers.ofInstance(EXPECTED);
    SupplierSequenceAdapter<Integer> sut = new SupplierSequenceAdapter<Integer>(candidate);

    //exercise sut
    Integer actual = sut.get();
    Integer actualSize = sut.size();

    //verify outcome
    assertThat(actual).isEqualTo(EXPECTED);
    assertThat(actualSize).isEqualTo(1);
  }

  @Test
  public void adapting_a_supplier_that_is_composed_of_another_sequence_should_result_in_a_sequence_with_the_same_size_as_the_owned_sequence(){


    //setup fixture
    SupplierWithOneSequence candidate = new SupplierWithOneSequence(createSequenceWithSize(3, 1, 2, 3));
    SupplierSequenceAdapter<Integer> sut = new SupplierSequenceAdapter<Integer>(candidate);

    //exercise sut
    List<Integer> actual = readSequence(sut, 3);
    Integer actualSize = sut.size();

    //verify outcome
    assertThat(actual).as("The inner sequence value multiplied by 3").containsExactly(3,6,9);
    assertThat(actualSize).isEqualTo(3);


  }

  private Sequence<Integer> createSequenceWithSize(int size, Integer first, Integer...results) {
    Sequence<Integer> sequence =mock(Sequence.class);
    when(sequence.size()).thenReturn(size);
    when(sequence.get()).thenReturn(first, results);
    return sequence;
  }

  private List<Integer> readSequence(Sequence<Integer> actual, int size) {
    List<Integer> actualSequence = Lists.newArrayList();
    for (int i = 0; i < size; i++) {
      actualSequence.add(actual.get());
    }
    return actualSequence;
  }

  public static class SupplierWithOneSequence implements Supplier<Integer>{

    private Sequence<Integer> innerSequence;

   public SupplierWithOneSequence(Sequence<Integer> innerSequence){
     this.innerSequence = innerSequence;
   }

    @Override
    public Integer get() {
      return innerSequence.get() * 3;
    }

  }


}
