package org.magenta;

import org.junit.Test;

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

  public static class SelfReferencingGenerationStrategy implements SimpleGenerationStrategy<String, DataSpecification>{

    @Override
    public String generateItem(DataDomain<? extends DataSpecification> dataDomain) {
      return dataDomain.dataset(String.class).any();
    }

    @Override
    public int getPreferredNumberOfItems(DataSpecification specification) {
      return specification.getDefaultNumberOfItems();
    }

  }

  public static class FooGenerationStrategy implements SimpleGenerationStrategy<String, DataSpecification>{

    @Override
    public String generateItem(DataDomain<? extends DataSpecification> dataDomain) {
      return dataDomain.dataset(BAR).any();
    }

    @Override
    public int getPreferredNumberOfItems(DataSpecification specification) {
      return specification.getDefaultNumberOfItems();
    }

  }

  public static class BarGenerationStrategy implements SimpleGenerationStrategy<String, DataSpecification>{

    @Override
    public String generateItem(DataDomain<? extends DataSpecification> dataDomain) {
      return dataDomain.dataset(FOO).any();
    }

    @Override
    public int getPreferredNumberOfItems(DataSpecification specification) {
      return specification.getDefaultNumberOfItems();
    }

  }

  public static class TicGenerationStrategy implements SimpleGenerationStrategy<String, DataSpecification>{

    @Override
    public String generateItem(DataDomain<? extends DataSpecification> dataDomain) {
      return dataDomain.dataset(TAC).any();
    }

    @Override
    public int getPreferredNumberOfItems(DataSpecification specification) {
      return specification.getDefaultNumberOfItems();
    }

  }

  public static class TacGenerationStrategy implements SimpleGenerationStrategy<String, DataSpecification>{

    @Override
    public String generateItem(DataDomain<? extends DataSpecification> dataDomain) {
      return dataDomain.dataset(TOE).any();
    }

    @Override
    public int getPreferredNumberOfItems(DataSpecification specification) {
      return specification.getDefaultNumberOfItems();
    }

  }

  public static class ToeGenerationStrategy implements SimpleGenerationStrategy<String, DataSpecification>{

    @Override
    public String generateItem(DataDomain<? extends DataSpecification> dataDomain) {
      return dataDomain.dataset(TIC).any();
    }

    @Override
    public int getPreferredNumberOfItems(DataSpecification specification) {
      return specification.getDefaultNumberOfItems();
    }

  }
}
