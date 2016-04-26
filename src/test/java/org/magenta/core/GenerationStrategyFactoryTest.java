package org.magenta.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.magenta.DataSet;
import org.magenta.Fixture;
import org.magenta.FixtureFactory;
import org.magenta.Magenta;
import org.magenta.Sequence;
import org.magenta.annotation.InjectDataSet;
import org.magenta.annotation.InjectSequence;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;


@RunWith(MockitoJUnitRunner.class)
public class GenerationStrategyFactoryTest {

  private GenerationStrategyFactory sut;

  @Before
  public void initInjector(){

    FixtureContext ctx = Magenta.modules().fixtureContext();

    this.sut = new GenerationStrategyFactory(ctx, Magenta.modules().injector());
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

    assertThat(strategy.size(fixture)).isEqualTo(Integer.MAX_VALUE);

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

  @Test
  public void injecting_a_sequence_into_a_generator() throws NoSuchFieldException, SecurityException{
    //setup fixtures
    Integer[] expected = new Integer[]{1,2,3,4,5,6};

    FixtureFactory fixture = Magenta.newFixture();

    fixture.newDataSet(Integer.class).composedOf(1,2,3,4,5,6);

    DummyGenerator2 candidate = new DummyGenerator2();

    //exercise sut

    GenerationStrategy<Integer> strategy = sut.create(candidate);

    //verify outcome

    List<Integer> actual = readFrom(strategy,6, fixture);

    assertThat(actual).containsExactly(expected);
    assertThat(strategy.size(fixture)).isEqualTo(6);
  }

  @Test
  public void injecting_two_sequences_into_a_generator() throws NoSuchFieldException, SecurityException{
    //setup fixtures

    String[] expected = new String[] { "a1", "b1", "a2", "b2", "a3", "b3", "a4", "b4", "a5", "b5", "a6", "b6" };


    FixtureFactory fixture = Magenta.newFixture();

    fixture.newDataSet(String.class).composedOf("a","b");
    fixture.newDataSet(Integer.class).composedOf(1,2,3,4,5,6);


    DummyGenerator3 candidate = new DummyGenerator3();

    //exercise sut

    GenerationStrategy<String> strategy = sut.create(candidate);

    //verify outcome

    List<String> actual = readFrom(strategy,expected.length, fixture);

    assertThat(actual).containsExactly(expected);
    assertThat(strategy.size(fixture)).isEqualTo(expected.length);
  }






  private <D> List<D> readFrom(GenerationStrategy<D> strategy, int size, FixtureFactory fixture) {
    List<D> values = Lists.newArrayList();
    for(int i=0;i<size;i++){
      values.add(strategy.generate(fixture));
    }
    return values;
  }

  public static class DummyGenerator implements Supplier<List<Integer>>{
    @InjectDataSet
    private DataSet<Integer> integers;

    @Override
    public List<Integer> get(){
      return integers.list();
    }
  }

  public static class DummyGenerator2 implements Supplier<Integer>{
    @InjectSequence
    private Sequence<Integer> integers;

    @Override
    public Integer get(){
      return integers.next();
    }
  }

  public static class DummyGenerator3 implements Supplier<String>{

    @InjectSequence
    private Sequence<String> strings;

    @InjectSequence
    private Sequence<Integer> integers;

    @Override
    public String get(){
      return strings.next()+integers.next();
    }
  }
}
