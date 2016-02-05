package org.magenta.core.data.supplier;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.Test;
import org.magenta.DataSupplier;
import org.magenta.random.FluentRandom;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;

public class GeneratorDataSupplierTest {

  @Test
  public void testGeneratorConstructor() {

    // setup fixtures
    final int expectedSize = 10;
    final TypeToken<String> expectedType = TypeToken.of(String.class);

    DataSupplier<String> sut = new GeneratorDataSupplier<String>(expectedType, generatorOfStrings(), Suppliers.ofInstance(expectedSize));

    // verify outcome
    assertThat(sut.getSize()).as("size").isEqualTo(expectedSize);
    assertThat(sut.getType()).as("type").isEqualTo(expectedType);

    assertThat(sut.isConstant()).as("constant").isFalse();
    assertThat(sut.isGenerated()).as("generated").isTrue();
    assertThat(sut.isEmpty()).as("empty").isFalse();

  }

  @Test
  public void testGetAt() {

    // setup fixtures
    final int expectedSize = 10;
    final TypeToken<String> expectedType = TypeToken.of(String.class);

    DataSupplier<String> sut = new GeneratorDataSupplier<String>(expectedType, generatorOfStrings(), Suppliers.ofInstance(expectedSize));

    // verify outcome
    assertThat(sut.get(0)).overridingErrorMessage("Same data was generated : {0}, was expecting different value  even from the same index",
        sut.get(0)).isNotEqualTo(sut.get(0));

  }

  @Test
  public void testIteration() {

    // setup fixtures
    final int expectedSize = 25;
    final TypeToken<String> expectedType = TypeToken.of(String.class);

    DataSupplier<String> sut = new GeneratorDataSupplier<String>(expectedType, generatorOfStrings(), Suppliers.ofInstance(expectedSize));

    // exercise sut
    Set<String> actual = Sets.newHashSet();

    for (String s : sut) {
      actual.add(s);
    }

    // verify outcome
    assertThat(actual.size()).isEqualTo(25);

  }

  private Supplier<String> generatorOfStrings() {

    return new Supplier<String>() {

      @Override
      public String get() {
        return FluentRandom.strings().charabia(25);
      }

    };
  }
}
