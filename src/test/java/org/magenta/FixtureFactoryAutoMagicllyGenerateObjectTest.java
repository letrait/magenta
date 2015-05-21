package org.magenta;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.magenta.FixtureFactory;
import org.magenta.Magenta;
import org.magenta.random.FluentRandom;
import org.magenta.testing.domain.PhoneNumber;

public class FixtureFactoryAutoMagicllyGenerateObjectTest {

  @Test
  public void auto_magically_generate_phone_numbers(){
    //setup fixture
    FixtureFactory sut = createRootFixtureFactory();

    //exercise sut
    sut.newDataSet(PhoneNumber.class).autoMagicallyGenerated(5);

    //verify outcome
    List<PhoneNumber> actual = sut.dataset(PhoneNumber.class).list();

    System.out.println(actual);

    assertThat(actual).extracting("phoneNumber").hasSize(5).doesNotContainNull();

  }

  private FixtureFactory createRootFixtureFactory() {
    return Magenta.newFixture(FluentRandom.get(new Random(1)));
  }
}
