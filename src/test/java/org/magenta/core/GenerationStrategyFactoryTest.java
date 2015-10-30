package org.magenta.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.magenta.DataSet;
import org.magenta.Fixture;
import org.magenta.FixtureFactory;
import org.magenta.Magenta;
import org.magenta.annotation.InjectDataSet;
import org.magenta.core.context.ThreadLocalFixtureContext;
import org.magenta.core.injector.extractors.HiearchicalFieldsExtractor;
import org.magenta.core.injector.handlers.DataSetFieldHandler;
import org.magenta.core.injector.handlers.DataSetFieldHandlerTest.DummyGenerator;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;


@RunWith(MockitoJUnitRunner.class)
public class GenerationStrategyFactoryTest {

  private GenerationStrategyFactory sut;
  
  @Before
  public void initInjector(){
    
    FixtureContext ctx = Magenta.dependencies.get().fixtureContext();
    
    this.sut = new GenerationStrategyFactory(ctx, Magenta.dependencies.get().injector(ctx));
  }
  
  @Test
  public void testASimpleGenerator(){
    
    //setup fixture
    Fixture fixture = mock(Fixture.class);
    
    Supplier<String> generator = mock(Supplier.class);
    
    when(generator.get()).thenReturn("a","b","c");
    
    //exercise sut
    GenerationStrategy<String> strategy = sut.create(generator);
    
    //verify outcome
    assertThat(strategy.generate(fixture)).isEqualTo("a");
    assertThat(strategy.generate(fixture)).isEqualTo("b");
    assertThat(strategy.generate(fixture)).isEqualTo("c");
    
    assertThat(strategy.size(fixture)).isEqualTo(1);
    
  }
  
  @Test
  public void injecting_a_dataset_into_a_generator() throws NoSuchFieldException, SecurityException{
  //setup fixtures
    Integer[] expected = new Integer[]{1,2,3,4,5,6};

    FixtureFactory fixture = Magenta.newFixture();

    fixture.newDataSet(Integer.class).composedOf(1,2,3,4,5,6);

    DummyGenerator candidate = new DummyGenerator();

    //exercise sut

    GenerationStrategy<List<Integer>> strategy = sut.create(candidate);

    //verify outcome

   assertThat(strategy.generate(fixture)).containsExactly(expected);
  }
  
  public static class DummyGenerator implements Supplier<List<Integer>>{
    @InjectDataSet
    private DataSet<Integer> integers;

    public List<Integer> get(){
      return integers.list();
    }
  }
}
