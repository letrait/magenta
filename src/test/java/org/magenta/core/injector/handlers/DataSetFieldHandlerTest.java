package org.magenta.core.injector.handlers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.Test;
import org.magenta.DataKey;
import org.magenta.DataSet;
import org.magenta.FixtureFactory;
import org.magenta.Magenta;
import org.magenta.annotation.InjectDataSet;
import org.magenta.core.injector.extractors.HiearchicalFieldsExtractor;
import org.mockito.Mockito;

import com.google.common.base.Suppliers;

public class DataSetFieldHandlerTest {

  @Test
  public void testInjectIntoFieldReturnFalseWhenNothingWasInjected() throws NoSuchFieldException, SecurityException{

    //setup fixtures

    FixtureFactory fixture = mock(FixtureFactory.class);

    Field f = DummyGeneratorWithNoInjectedDataSet.class.getDeclaredField("integers");
    DummyGeneratorWithNoInjectedDataSet candidate = new DummyGeneratorWithNoInjectedDataSet();

    DataSetFieldHandler sut = new DataSetFieldHandler(HiearchicalFieldsExtractor.SINGLETON);

    //exercise sut
    sut.injectInto(candidate, Suppliers.ofInstance(fixture));

    //verify outcome
    //The injection is a proxy on a dataset, the dataset should not be retrieved yet
    verify(fixture,Mockito.never()).dataset(Mockito.any(DataKey.class));

  }

  @Test
  public void testDataSetIsObtainedFromFixtureContext() throws NoSuchFieldException, SecurityException{

    //setup fixtures

    FixtureFactory fixture = mock(FixtureFactory.class);

    Field f = DummyGenerator.class.getDeclaredField("integers");
    DummyGenerator candidate = new DummyGenerator();

    DataSetFieldHandler sut = new DataSetFieldHandler(HiearchicalFieldsExtractor.SINGLETON);

    //exercise sut
    sut.injectInto(candidate, Suppliers.ofInstance(fixture));

    //verify outcome


    //The injection is a proxy on a dataset, the dataset should not be retrieved yet
    verify(fixture,Mockito.never()).dataset(Mockito.any(DataKey.class));

  }

  @Test
  public void testDataSetInjection() throws NoSuchFieldException, SecurityException{

    //setup fixtures
    Integer[] expected = new Integer[]{1,2,3,4,5,6};

    FixtureFactory fixture = Magenta.newFixture();

    fixture.newDataSet(Integer.class).composedOf(1,2,3,4,5,6);

    Field f = DummyGenerator.class.getDeclaredField("integers");
    DummyGenerator candidate = new DummyGenerator();

    DataSetFieldHandler sut = new DataSetFieldHandler(HiearchicalFieldsExtractor.SINGLETON);

    //exercise sut

    sut.injectInto(candidate, Suppliers.ofInstance(fixture));

    //verify outcome

   assertThat(candidate.get()).containsExactly(expected);
  }



  public static class DummyGenerator{
    @InjectDataSet
    private DataSet<Integer> integers;

    public List<Integer> get(){
      return integers.list();
    }
  }

  public static class DummyGeneratorWithNoInjectedDataSet{
    private DataSet<Integer> integers;

    public List<Integer> get(){
      return integers.list();
    }
  }
}
