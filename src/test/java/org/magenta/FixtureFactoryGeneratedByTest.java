package org.magenta;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.magenta.testing.domain.Address;
import org.magenta.testing.domain.AddressGenerator;
import org.magenta.testing.domain.Employee;
import org.magenta.testing.domain.EmployeeGenerator;
import org.magenta.testing.domain.EmployeeGenerator2;
import org.magenta.testing.domain.EmployeeGenerator3;
import org.magenta.testing.domain.Occupation;
import org.magenta.testing.domain.PhoneNumber;
import org.magenta.testing.domain.PhoneNumberGenerator;

import com.google.common.reflect.TypeToken;

public class FixtureFactoryGeneratedByTest {

  @Test
  public void testMissingSequenceForGenerator() {

    // setup fixtures

    FixtureFactory fixtures = createRootFixtureFactory();
    // exercise sut

    try {
      fixtures.newDataSet(Employee.class).generatedBy(new EmployeeGenerator());
      DataSet<Employee> actual = fixtures.dataset(Employee.class);
      actual.any();
      fail("expecting"+DataGenerationException.class.getName());
    } catch (DataGenerationException dge) {
      assertThat(dge).hasMessageContaining("Employee").hasMessageContaining("EmployeeGenerator").hasRootCauseInstanceOf(DataSetNotFoundException.class);
    }

  }


  @Test
  public void testAGeneratorUsingAPredeterminedInjectedSequence(){

    //setup fixtures
    Occupation[] expectedOccupation = new Occupation[]{Occupation.ENGINEER, Occupation.MANAGEMENT, Occupation.TECHNICIAN, Occupation.TESTER};

    FixtureFactory fixtures = createRootFixtureFactory();
    fixtures.newDataSet(Occupation.class).composedOf(expectedOccupation);
    //exercise sut


    fixtures.newDataSet(Employee.class).generatedBy(new EmployeeGenerator());
    DataSet<Employee> actual = fixtures.dataset(Employee.class);

    //verify outcome
    assertThat(actual.getType()).isEqualTo(TypeToken.of(Employee.class));
    assertThat(actual.list(4)).extracting("occupation").containsExactly(expectedOccupation);
  }

  @Test
  public void testAGeneratorUsingDifferentInjectedSequence(){

    //setup fixtures
    Occupation[] expectedOccupation = new Occupation[]{Occupation.ENGINEER, Occupation.MANAGEMENT, Occupation.TECHNICIAN, Occupation.TESTER};

    FixtureFactory fixtures = createRootFixtureFactory();
    fixtures.newDataSet(Occupation.class).composedOf(expectedOccupation);
    fixtures.newGenerator(Address.class).generatedBy(new AddressGenerator());
    fixtures.newDataSet(Employee.class).generatedBy(new EmployeeGenerator2());
    DataSet<Employee> actual = fixtures.dataset(Employee.class);

    //exercise sut and verify outcome

    assertThat(actual.getType()).isEqualTo(TypeToken.of(Employee.class));
    assertThat(actual.list(4)).extracting("occupation").containsExactly(expectedOccupation);
    assertThat(actual.list(4)).extracting("address").doesNotContainNull();
  }

  @Test
  public void testAGeneratorUsingAnInjectedDataSet(){

    //setup fixtures
    FixtureFactory fixtures = createRootFixtureFactory();
    fixtures.newDataSet(Occupation.class).composedOf(Occupation.values());
    fixtures.newDataSet(Address.class).generatedBy(new AddressGenerator());
    fixtures.newGenerator(PhoneNumber.class).generatedBy(new PhoneNumberGenerator());
    fixtures.newDataSet(Employee.class).generatedBy(new EmployeeGenerator3());
    DataSet<Employee> actual = fixtures.dataset(Employee.class);

    //exercise sut and verify outcome

    assertThat(actual.getType()).isEqualTo(TypeToken.of(Employee.class));
    assertThat(actual.any().getPhoneNumbers()).isNotEmpty();
    System.out.println(actual.list());
  }

  @Test
  public void testAGeneratorWithASpecifiedDefaultSize(){

    //setup fixtures
    FixtureFactory fixtures = createRootFixtureFactory();

    //exercise sut
    fixtures.newDataSet(PhoneNumber.class).generatedBy(new PhoneNumberGenerator(), 20);
    DataSet<PhoneNumber> actual =fixtures.dataset(PhoneNumber.class);

    //verify outcome
    assertThat(actual.list()).hasSize(20);
  }

  @Test
  public void testAGeneratorWithASpecificSize(){

    //setup fixtures
    FixtureFactory fixtures = createRootFixtureFactory();

    //exercise sut
    fixtures.newDataSet(PhoneNumber.class).generatedBy(new PhoneNumberGenerator(), 20);
    DataSet<PhoneNumber> actual =fixtures.dataset(PhoneNumber.class);

    //verify outcome
    assertThat(actual.list(30)).hasSize(30);
  }

  private FixtureFactory createRootFixtureFactory() {
    return Magenta.newFixture();
  }
}
