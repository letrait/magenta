package org.magenta.core.automagic.generation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.magenta.FixtureFactory;
import org.magenta.Magenta;
import org.magenta.core.injector.extractors.HiearchicalFieldsExtractor;

import com.google.common.base.Suppliers;
import com.google.common.reflect.TypeToken;

public class ReflexionBasedGeneratorFactoryObjectUseCaseTest extends AbstractReflexionBasedGeneratorFactoryTest {

  @Test
  public void testGenerationOfAValueObject(){
    //setup fixture
    FixtureFactory fixture = Magenta.newFixture();
    ReflexionBasedGeneratorFactory sut = new ReflexionBasedGeneratorFactory(Suppliers.ofInstance(fixture), dataKeyMapBuilder(), HiearchicalFieldsExtractor.SINGLETON);

    //exercise sut
    Foo actual = sut.buildGeneratorOf(TypeToken.of(Foo.class), fixture).get();

    //verify outcome
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

  }
}
