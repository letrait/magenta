package org.magenta;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.magenta.DataKey;
import org.magenta.DataSet;
import org.magenta.FixtureFactory;
import org.magenta.Magenta;
import org.magenta.random.FluentRandom;

import com.google.common.reflect.TypeToken;

public class FixtureFactoryComposedOfTest {

  @Test
  public void testComposedOfWithAnIterable(){

    Integer[] expectedNumbers = new Integer[]{1,2,3,4,5,6,7};
    Iterable<Integer>   integers = Arrays.asList(expectedNumbers);
    FixtureFactory fixtures = createRootFixtureFactory();

    DataKey<Integer> key = DataKey.of(Integer.class);

    //exercise sut
    fixtures.newDataSet(key).composedOf(integers);
    DataSet<Integer> actual = fixtures.dataset(key);

    //verify outcome
    assertThat(actual).containsExactly(expectedNumbers);
    assertThat(actual.isConstant()).isTrue();
    assertThat(actual.isEmpty()).isFalse();
    assertThat(actual.isGenerated()).isFalse();
    assertThat(actual.getSize()).isEqualTo(expectedNumbers.length);
    assertThat(actual.getMaximumSize()).isEqualTo(expectedNumbers.length);
    assertThat(actual.getType()).isEqualTo(key.getType());
    assertThat(actual.any()).isIn(expectedNumbers);


  }

  @Test
  public void testComposedOfTypeToken(){

    List<Integer>[] expectedSets = new List[]{Arrays.asList(1,2,3), Arrays.asList(4,5,6), Arrays.asList(7,8,9)};

    FixtureFactory fixtures = createRootFixtureFactory();

    DataKey<List<Integer>> key = DataKey.of(new TypeToken<List<Integer>>(){});

    //exercise sut
    fixtures.newDataSet(key).composedOf(expectedSets);

    DataSet<List<Integer>> actual = fixtures.dataset(key);

    //verify outcome
    assertThat(actual).containsExactly(expectedSets);
    assertThat(actual.isConstant()).isTrue();
    assertThat(actual.isEmpty()).isFalse();
    assertThat(actual.isGenerated()).isFalse();
    assertThat(actual.getSize()).isEqualTo(expectedSets.length);
    assertThat(actual.getMaximumSize()).isEqualTo(expectedSets.length);
    assertThat(actual.getType()).isEqualTo(key.getType());
    assertThat(actual.any()).isIn(expectedSets);


  }

  private FixtureFactory createRootFixtureFactory() {
    return Magenta.newFixture(FluentRandom.singleton());
  }
}
