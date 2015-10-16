package org.magenta.core.automagic.generation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.magenta.FixtureFactory;
import org.magenta.Magenta;
import org.magenta.core.injector.extractors.HiearchicalFieldsExtractor;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;

public class ReflexionBasedGeneratorFactoryEnumUseCaseTest extends AbstractReflexionBasedGeneratorFactoryTest {

  @Test(expected = UnsupportedOperationException.class)
  public void testGenerationOfAnEnumObjectShouldNotBeSupported() {
    // setup fixture
    FixtureFactory fixture = Magenta.newFixture();
    ReflexionBasedGeneratorFactory sut = new ReflexionBasedGeneratorFactory(Suppliers.ofInstance(fixture), dataKeyMapBuilder(), HiearchicalFieldsExtractor.SINGLETON);

    // exercise sut
    Color actual = sut.buildGeneratorOf(TypeToken.of(Color.class),fixture).get();
  }



  @Test
  public void testGenerationOfAValueObjectWithAnEnum() {
    // setup fixture
    FixtureFactory fixture = Magenta.newFixture();

    ReflexionBasedGeneratorFactory sut = new ReflexionBasedGeneratorFactory(Suppliers.ofInstance(fixture), dataKeyMapBuilder(), HiearchicalFieldsExtractor.SINGLETON);

    // exercise sut
    List<Car> actual = Lists.newArrayList();

    Supplier<Car> gen= sut.buildGeneratorOf(TypeToken.of(Car.class),fixture);;

    for (int i = 0; i < 3; i++) {

      actual.add(gen.get());
    }

    // verify outcome
    assertThat(actual).extracting("color").containsExactly(Color.RED, Color.BLUE, Color.GREEN);

  }

  public static enum Color {

    RED, BLUE, GREEN;

  }

  public static class Car {

    private Color color;

  }
}
