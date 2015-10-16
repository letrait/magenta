package org.magenta.core.automagic.generation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.magenta.FixtureFactory;
import org.magenta.Magenta;
import org.magenta.core.injector.extractors.HiearchicalFieldsExtractor;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.reflect.TypeToken;

public class ReflexionBasedGeneratorFactoryPrimitiveUseCaseTest extends AbstractReflexionBasedGeneratorFactoryTest {

  @Test
  public void test_a_simple_generator_of_string(){

    //Setup fixture
    FixtureFactory fixture = Magenta.newFixture();
    ReflexionBasedGeneratorFactory sut = new ReflexionBasedGeneratorFactory(Suppliers.ofInstance(fixture), dataKeyMapBuilder(), HiearchicalFieldsExtractor.SINGLETON);


    //exercise sut
    Supplier<String> actual =sut.buildGeneratorOf(TypeToken.of(String.class), fixture);

    //verify outcome
    assertThat(actual.get()).isNotNull();

  }

  @Test
  public void test_a_simple_generator_of_integer(){

    //Setup fixture
    FixtureFactory fixture = Magenta.newFixture();
    ReflexionBasedGeneratorFactory sut = new ReflexionBasedGeneratorFactory(Suppliers.ofInstance(fixture), dataKeyMapBuilder(), HiearchicalFieldsExtractor.SINGLETON);


    //exercise sut
    Supplier<Integer> actual =sut.buildGeneratorOf(TypeToken.of(Integer.class), fixture);


    //verify outcome
    assertThat(actual.get()).isNotNull();

  }

  @Test
  public void test_a_simple_generator_of_long(){

    //Setup fixture
    FixtureFactory fixture = Magenta.newFixture();
    ReflexionBasedGeneratorFactory sut = new ReflexionBasedGeneratorFactory(Suppliers.ofInstance(fixture), dataKeyMapBuilder(), HiearchicalFieldsExtractor.SINGLETON);


    //exercise sut
    Supplier<Long> actual =sut.buildGeneratorOf(TypeToken.of(Long.class), fixture);


    //verify outcome
    assertThat(actual.get()).isNotNull().isInstanceOf(Long.class);

  }

}
