package org.magenta;

import org.junit.Test;
import org.magenta.annotations.InjectDataSet;

import com.google.common.base.Supplier;

public class CycleDetectionTestCase extends DataDomainTestSupport{

  @Test(expected=CycleDetectedInGenerationException.class)

  public void test_one_generator_referencing_itself_should_be_detected(){

    //setup fixtures
    DataDomainManager<SimpleDataSpecification> sut = createAnonymousDomain();

    sut.newDataSet(String.class).generatedBy(new SelfReferencingGenerationStrategy());

    //exercise sut
    sut.dataset(String.class).any();
  }


  private static final DataKey<String> FOO =DataKey.makeQualified("Foo", String.class);
  private static final DataKey<String> BAR =DataKey.makeQualified("Bar", String.class);

  @Test(expected=CycleDetectedInGenerationException.class)
  public void test_two_generators_referencing_each_other_should_be_detected(){
    //setup fixtures
    DataDomainManager<SimpleDataSpecification> sut = createAnonymousDomain();

    sut.newDataSet(FOO).generatedBy(new FooGenerationStrategy());
    sut.newDataSet(BAR).generatedBy(new BarGenerationStrategy());

    //exercise sut
    sut.dataset(FOO).any();


  }

  private static final DataKey<String> TIC =DataKey.makeQualified("Tic", String.class);
  private static final DataKey<String> TAC =DataKey.makeQualified("Tac", String.class);
  private static final DataKey<String> TOE =DataKey.makeQualified("Toe", String.class);

  @Test(expected=CycleDetectedInGenerationException.class)
  public void test_three_generators_referencing_each_other_should_be_detected(){
    //setup fixtures
    DataDomainManager<SimpleDataSpecification> sut = createAnonymousDomain();

    sut.newDataSet(TIC).generatedBy(new TicGenerationStrategy());
    sut.newDataSet(TAC).generatedBy(new TacGenerationStrategy());
    sut.newDataSet(TOE).generatedBy(new ToeGenerationStrategy());

    //exercise sut
    sut.dataset(TIC).any();


  }

  public static class SelfReferencingGenerationStrategy implements Supplier<String>{

    @InjectDataSet
    DataSet<String> self;

    @Override
    public String get() {
      return self.any();
    }


  }

  public static class FooGenerationStrategy implements Supplier<String>{

    @InjectDataSet("Bar")
    DataSet<String> bar;

    @Override
    public String  get() {
      return bar.any();
    }


  }

  public static class BarGenerationStrategy implements Supplier<String>{

    @InjectDataSet("Foo")
    DataSet<String> foo;

    @Override
    public String  get() {
      return foo.any();
    }


  }

  public static class TicGenerationStrategy implements Supplier<String>{

    @InjectDataSet("Tac")
    DataSet<String> tac;

    @Override
    public String  get() {
      return tac.any();
    }

  }

  public static class TacGenerationStrategy implements Supplier<String>{


    @InjectDataSet("Toe")
    DataSet<String> toe;

    @Override
    public String  get() {
      return toe.any();
    }


  }

  public static class ToeGenerationStrategy implements Supplier<String>{


    @InjectDataSet("Tic")
    DataSet<String> tic;

    @Override
    public String get() {
      return tic.any();
    }


  }
}
