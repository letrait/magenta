package org.magenta.core.automagic.generation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.magenta.core.automagic.generation.ReflexionBasedGeneratorFactory;
import org.magenta.core.injector.extractors.HiearchicalFieldsExtractor;
import org.magenta.random.FluentRandom;

import com.google.common.base.Supplier;
import com.google.common.reflect.TypeToken;

public class ReflexionBasedGeneratorFactoryPrimitiveUseCaseTest {

  @Test
  public void test_a_simple_generator_of_string(){

    //Setup fixture
    ReflexionBasedGeneratorFactory sut = new ReflexionBasedGeneratorFactory(FluentRandom.singleton(), HiearchicalFieldsExtractor.SINGLETON);

    //exercise sut
    Supplier<String> actual =sut.buildGeneratorOf(TypeToken.of(String.class));

    //verify outcome
    assertThat(actual.get()).isNotNull();

  }

  @Test
  public void test_a_simple_generator_of_integer(){

    //Setup fixture
    ReflexionBasedGeneratorFactory sut = new ReflexionBasedGeneratorFactory(FluentRandom.singleton(), HiearchicalFieldsExtractor.SINGLETON);

    //exercise sut
    Supplier<Integer> actual =sut.buildGeneratorOf(TypeToken.of(Integer.class));


    //verify outcome
    assertThat(actual.get()).isNotNull();

  }
}
