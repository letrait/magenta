package org.magenta.core.automagic.generation.provider.fields.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.magenta.FixtureFactory;
import org.magenta.Magenta;

public class DataSetFieldHydraterTest {

  @Test
  public void testHydrateAClassWithoutListFields() throws IllegalArgumentException, IllegalAccessException{

    //setup fixtures
    FixtureFactory fixture = Magenta.newFixture();
    DataSetFieldHydrater sut = new DataSetFieldHydrater();

    ClassWithoutList candidate = new ClassWithoutList();

    //exercise sut
    sut.hydrate(candidate, fixture);

    //then no exceptions
  }

  @Test
  public void testHydrateAClassWithAListAttributeWhoseTypeMatchAnEmptyDataSetInTheFixtures() throws IllegalArgumentException, IllegalAccessException{

    //setup fixtures
    FixtureFactory fixture = Magenta.newFixture();
    DataSetFieldHydrater sut = new DataSetFieldHydrater();

    fixture.newDataSet(String.class).composedOf("abc");

    ClassWithAList candidate = new ClassWithAList();

    //exercise sut
    sut.hydrate(candidate, fixture);

    //then
    assertThat(candidate.strings).isNotNull().containsExactly("abc");
  }

  public static class ClassWithoutList {


  }


  public static class ClassWithAList {

    List<String> strings;

  }
}
