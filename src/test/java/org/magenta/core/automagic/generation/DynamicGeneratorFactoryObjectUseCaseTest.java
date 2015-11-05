package org.magenta.core.automagic.generation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.magenta.FixtureFactory;
import org.magenta.Magenta;
import org.magenta.core.GenerationStrategy;
import org.magenta.core.injector.extractors.HiearchicalFieldsExtractor;

import com.google.common.base.Optional;
import com.google.common.base.Suppliers;
import com.google.common.reflect.TypeToken;

public class DynamicGeneratorFactoryObjectUseCaseTest extends AbstractReflexionBasedGeneratorFactoryTest {

  @Test
  public void testGenerationOfANonApplicableObject(){
    //setup fixture
    FixtureFactory fixture = Magenta.newFixture();
    DynamicGeneratorFactory sut = buildDynamicGeneratorFactory();

    //exercise sut
    //The bar class does not have a no-arg constructor, therefore no generation strategy should be returned
    Optional<GenerationStrategy<Bar>> actual = sut.buildGeneratorOf(TypeToken.of(Bar.class), fixture, sut);

    //verify outcome
    assertThat(actual.isPresent()).as("the presence of a generation strategy").isFalse();

  }
  
  @Test
  public void testGenerationOfAValueObject(){
    //setup fixture
    FixtureFactory fixture = Magenta.newFixture();
    DynamicGeneratorFactory sut = buildDynamicGeneratorFactory();

    //exercise sut
    Foo actual = sut.buildGeneratorOf(TypeToken.of(Foo.class), fixture, sut).get().generate(fixture);

    //verify outcome
    assertThat(actual.bar).as("The bar attribute").isNull();
    assertThat(actual.aString).isNotNull();
    assertThat(actual.anInteger).isNotNull();
    assertThat(actual.aLong).isNotNull();

    assertThat(actual.aPrimitiveInteger).isGreaterThan(0);
    assertThat(actual.aPrimitiveLong).isGreaterThan(0);
  }
  



  public static class Foo{

    private String aString;
    private Integer anInteger;
    private Long aLong;

    private int aPrimitiveInteger;
    private long aPrimitiveLong;
    
    private Bar bar;

  }
  
  public static class Bar {
    public Bar(String arg1, String arg2){
      
    }
  }
}
