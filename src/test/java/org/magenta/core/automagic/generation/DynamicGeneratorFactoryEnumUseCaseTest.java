package org.magenta.core.automagic.generation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.magenta.FixtureFactory;
import org.magenta.Magenta;
import org.magenta.core.GenerationStrategy;
import org.magenta.core.injector.extractors.HiearchicalFieldsExtractor;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;

public class DynamicGeneratorFactoryEnumUseCaseTest extends AbstractReflexionBasedGeneratorFactoryTest {


  @Test
  public void testGenerationOfAValueObjectWithAnEnum() {
    // setup fixture
    FixtureFactory fixture = Magenta.newFixture();

    DynamicGeneratorFactory sut = buildDynamicGeneratorFactory();

    // exercise sut
    List<Car> actual = Lists.newArrayList();

    GenerationStrategy<Car> gen= sut.buildGeneratorOf(TypeToken.of(Car.class),fixture, sut).get();

    for (int i = 0; i < 3; i++) {

      actual.add(gen.generate(fixture));
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
