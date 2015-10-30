package org.magenta;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.magenta.testing.domain.company.Address;
import org.magenta.testing.domain.company.AddressGenerator;
import org.magenta.testing.domain.company.Employee;
import org.magenta.testing.domain.company.EmployeeGenerator2;
import org.magenta.testing.domain.company.Occupation;

public class FixtureFactoryRestrictToUseCaseTest {

  private Fixture fixtures;

  @Before
  public void setupFixtures(){
    FixtureFactory fixtures = Magenta.newFixture();
    fixtures.newDataSet(Occupation.class).composedOf(Occupation.ENGINEER, Occupation.MANAGEMENT, Occupation.TECHNICIAN, Occupation.TESTER);
    fixtures.newGenerator(Address.class).generatedBy(new AddressGenerator());
    fixtures.newDataSet(Employee.class).generatedBy(new EmployeeGenerator2());
    this.fixtures = fixtures;

  }

  @Test
  public void testRestrictToOneObject(){

    //setup fixtures

    //exercise sut
    List<Employee> engineers = fixtures.restrictTo(Occupation.ENGINEER).dataset(Employee.class).list(3);

    //verify outcome
    assertThat(engineers).extracting("occupation").containsOnly(Occupation.ENGINEER);
  }

  @Test
  public void testRestrictToTwoObjects(){

    //setup fixtures
    Address expectedAddress = new Address();

    //exercise sut
    List<Employee> engineers = fixtures.restrictTo(Occupation.ENGINEER, expectedAddress).dataset(Employee.class).list(3);

    //verify outcome
    assertThat(engineers).extracting("occupation").containsOnly(Occupation.ENGINEER);
    assertThat(engineers).extracting("address").containsOnly(expectedAddress);
  }

  @Test
  public void testRestrictToDoesNotModifyTheRootFixture(){

    //setup fixtures

    //exercise sut
    List<Employee> engineers = fixtures.restrictTo(Occupation.ENGINEER).dataset(Employee.class).list(3);
    List<Employee> employees = fixtures.dataset(Employee.class).list(4);

    //verify outcome
    assertThat(engineers).extracting("occupation").containsOnly(Occupation.ENGINEER);
    assertThat(fixtures.dataset(Occupation.class).list()).containsExactly(Occupation.ENGINEER, Occupation.MANAGEMENT, Occupation.TECHNICIAN, Occupation.TESTER);
    assertThat(employees).extracting("occupation").containsExactly(Occupation.ENGINEER, Occupation.MANAGEMENT, Occupation.TECHNICIAN, Occupation.TESTER);
  }

  @Test
  public void test_single_restricting_will_alternate_on_addresses() {

    // setup fixtures
    Address[] addresses = fixtures.dataset(Address.class).array(3);

    // exercise sut
    List<Employee> employees = fixtures.restrictTo(addresses).dataset(Employee.class).list(6);

    // verify outcome
    assertThat(employees).extracting("address").containsExactly(addresses[0], addresses[1], addresses[2], addresses[0], addresses[1], addresses[2]);

  }

  @Test
  public void test_single_restricting_will_alternating_on_occupation() {

    // setup fixtures

    // exercise sut
    List<Employee> employees = fixtures.restrictTo(Occupation.ENGINEER, Occupation.TECHNICIAN).dataset(Employee.class).list(10);

    // verify outcome
    assertThat(employees).extracting("occupation").containsExactly(
        Occupation.ENGINEER, Occupation.TECHNICIAN,
        Occupation.ENGINEER, Occupation.TECHNICIAN,
        Occupation.ENGINEER, Occupation.TECHNICIAN,
        Occupation.ENGINEER, Occupation.TECHNICIAN,
        Occupation.ENGINEER, Occupation.TECHNICIAN);


  }


  @Test
  public void test_restricting_on_two_types_of_object_will_generate_all_possible_combination_alternating_on_addresses() {

    // setup fixtures
    Address[] addresses = fixtures.dataset(Address.class).array(3);

    // exercise sut
    List<Employee> employees = fixtures.restrictTo(addresses, Occupation.ENGINEER, Occupation.TECHNICIAN).dataset(Employee.class).list();

    // verify outcome
    assertThat(employees).extracting("occupation").containsExactly(Occupation.ENGINEER, Occupation.ENGINEER, Occupation.ENGINEER,
        Occupation.TECHNICIAN, Occupation.TECHNICIAN, Occupation.TECHNICIAN);
    assertThat(employees).extracting("address").containsExactly(addresses[0], addresses[1], addresses[2], addresses[0], addresses[1], addresses[2]);

  }

  @Test
  public void test_restricting_on_two_types_of_object_will_generate_all_possible_combination_alternating_on_occupation() {

    // setup fixtures
    Address[] addresses = fixtures.dataset(Address.class).array(3);

    // exercise sut
    List<Employee> employees = fixtures.restrictTo(Occupation.ENGINEER, Occupation.TECHNICIAN, addresses).dataset(Employee.class).list();

    // verify outcome
    assertThat(employees).extracting("occupation").containsExactly(Occupation.ENGINEER, Occupation.TECHNICIAN, Occupation.ENGINEER,
        Occupation.TECHNICIAN, Occupation.ENGINEER, Occupation.TECHNICIAN);
    assertThat(employees).extracting("address").containsExactly(addresses[0], addresses[0], addresses[1], addresses[1], addresses[2], addresses[2]);

  }

  //@Test : first x objects must have the first x restricted values, e.g. : restricitng with blue, green, red should result in the first three cars
  //having those three different colors, even if the color is not the first injected sequence of the generator of car (the maker is the first sequence)





}
