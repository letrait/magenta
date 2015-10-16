package org.magenta.core.injector.handlers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Test;
import org.magenta.DataKey;
import org.magenta.FixtureFactory;
import org.magenta.Magenta;
import org.magenta.Sequence;
import org.magenta.annotation.InjectSequence;
import org.magenta.core.injector.extractors.HiearchicalFieldsExtractor;
import org.mockito.Mockito;

import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;

public class SequenceFieldHandlerTest {

  @Test
  public void testSequenceIsObtainedFromFixtureContext() throws NoSuchFieldException, SecurityException{

    //setup fixtures

    FixtureFactory fixture = mock(FixtureFactory.class);

    DummyGenerator candidate = new DummyGenerator();

    SequenceFieldHandler sut = new SequenceFieldHandler(HiearchicalFieldsExtractor.SINGLETON);

    //exercise sut

    sut.injectInto( candidate, Suppliers.ofInstance(fixture));


    //The injection is a proxy on a dataset, the dataset should not be retrieved yet
    verify(fixture,Mockito.never()).dataset(Mockito.any(DataKey.class));

  }

  @Test
  public void testSequenceInjection() throws NoSuchFieldException, SecurityException{

    //setup fixtures
    Integer[] expected = new Integer[]{1,2,3,4,5,6};

    FixtureFactory fixture = Magenta.newFixture();

    fixture.newDataSet(Integer.class).composedOf(1,2,3,4,5,6);

    DummyGenerator candidate = new DummyGenerator();

    SequenceFieldHandler sut = new SequenceFieldHandler(HiearchicalFieldsExtractor.SINGLETON);

    //exercise sut

    sut.injectInto(candidate,  Suppliers.ofInstance(fixture));

    //verify outcome
    for(Integer e:expected){
      assertThat(candidate.get()).isEqualTo(e);
    }

  }


  @Test
  public void testCoordinatedSequenceInjection() throws NoSuchFieldException, SecurityException{

    //setup fixtures

    FixtureFactory fixture = Magenta.newFixture();

    fixture.newDataSet(Integer.class).composedOf(1,2,3);

    DummyGeneratorWithTwoSequences candidate = new DummyGeneratorWithTwoSequences();

    SequenceFieldHandler sut = new SequenceFieldHandler(HiearchicalFieldsExtractor.SINGLETON);

    //exercise sut

    sut.injectInto(candidate, Suppliers.ofInstance(fixture));

    //verify outcome
    List<Integer> actualSequence= Lists.newArrayList();

    for(int i = 0;i<9;i++){
      Integer[] r = candidate.get();
      actualSequence.add(r[0]);
      actualSequence.add(r[1]);
    }

    assertThat(actualSequence).as(actualSequence.toString()).containsExactly(1,1,2,1,3,1,1,2,2,2,3,2,1,3,2,3,3,3);

  }



  public class DummyGenerator{
    @InjectSequence
    private Sequence<Integer> integers;

    public Integer get(){
      return integers.get();
    }
  }

  public class DummyGeneratorWithTwoSequences{
    @InjectSequence
    private Sequence<Integer> integers1;

    @InjectSequence
    private Sequence<Integer> integers2;

    public Integer[] get(){
      return new Integer[]{integers1.get(), integers2.get()};
    }
  }


}
