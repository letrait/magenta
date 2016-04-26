package org.magenta;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

public class FixtureFactoryMaterializedFromUseCaseTest {

  @Test
  public void testSimpleMaterialization() {

    //setup fixture
    FixtureFactory fixture = Magenta.newFixture();
    fixture.newDataSet(String.class).transformed((Integer i)->i.toString()).materializedFrom(Integer.class);
    fixture.newDataSetOf(1,2,3,4,5);

    //exercise sut
    List<String> numbers = fixture.dataset(String.class).list();

    //verify outcome
    assertThat(numbers).containsExactly("1","2","3","4","5");
  }

  @Test
  public void testIndirectRestrictionEffect(){
    //setup fixture
    FixtureFactory fixture = Magenta.newFixture();
    fixture.newDataSet(String.class).transformed((Integer i)->i.toString()).materializedFrom(Integer.class);
    fixture.newDataSetOf(1,2,3,4,5);

    //exercise sut
    List<String> numbers = fixture.restrictTo(9,10).dataset(String.class).list();

    //verify outcome
    assertThat(numbers).containsExactly("9","10");
  }
}
