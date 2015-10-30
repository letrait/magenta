package org.magenta;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.magenta.testing.domain.company.PhoneNumber;
import org.magenta.testing.domain.company.PhoneNumberGenerator;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;

public class FixtureFactoryNewGeneratorGeneratedByTest {

  @Test
  public void testAGeneratorUsingDifferentSequence(){

    //setup fixtures

    FixtureFactory fixtures = createRootFixtureFactory();

     fixtures.newGenerator(PhoneNumber.class).generatedBy(new PhoneNumberGenerator(), 2);
     DataSet<PhoneNumber> actual = fixtures.dataset(PhoneNumber.class);

    //exercise sut and verify outcome
    List<PhoneNumber> phoneNumbers = Lists.newArrayList();
    for(int i = 0 ; i<3 ;i++){
      phoneNumbers.add(actual.any());
    }
    assertThat(phoneNumbers).doesNotHaveDuplicates();

    assertThat(actual.isConstant()).isFalse();
    assertThat(actual.isGenerated()).isTrue();
    assertThat(actual.isEmpty()).isFalse();
    assertThat(actual.getSize()).isEqualTo(2);
    assertThat(actual.getType()).isEqualTo(TypeToken.of(PhoneNumber.class));
  }

  private FixtureFactory createRootFixtureFactory() {
    return Magenta.newFixture();
  }
}
