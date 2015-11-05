package org.magenta.core.automagic.generation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.magenta.FixtureFactory;
import org.magenta.Magenta;
import org.magenta.core.GenerationStrategy;

import com.google.common.base.Optional;
import com.google.common.reflect.TypeToken;

@RunWith(Parameterized.class)
public class DynamicGeneratorFactoryPrimitiveUseCaseTest extends AbstractReflexionBasedGeneratorFactoryTest {

  @Parameters(name = "{index}: for {0}")
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] { 
      { String.class, String.class }, { Integer.class,Integer.class }, { Double.class, Double.class }, { Short.class, Short.class }, 
      { Long.class, Long.class }, { Float.class, Float.class },
        { int.class, Integer.class }, { long.class, Long.class }, { short.class, Short.class }, { double.class, Double.class }, { float.class, Float.class } });
  }

  private Class<?> inputType;
  private Class<?> expectedType;

  public DynamicGeneratorFactoryPrimitiveUseCaseTest(Class<?> type, Class<?> expectedType) {
    this.inputType = type;
    this.expectedType = expectedType;
  }

  @Test
  public void test_a_simple_generator_of_string() {

    // Setup fixture
    FixtureFactory fixture = Magenta.newFixture();
    DynamicGeneratorFactory sut = buildDynamicGeneratorFactory();

    // exercise sut
    Optional<GenerationStrategy<?>> actual = (Optional) sut.buildGeneratorOf(TypeToken.of(inputType), fixture, sut);

    // verify outcome
    assertThat(actual.isPresent()).as("the presence of a generation strategy for type " + inputType.getName()).isTrue();
    assertThat(actual.get().generate(fixture)).isNotNull().isInstanceOf(expectedType);

  }

}
