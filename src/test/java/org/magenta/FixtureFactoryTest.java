package org.magenta;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;

import java.util.List;

import org.junit.Test;
import org.magenta.testing.domain.Employee;
import org.magenta.testing.domain.Occupation;
import org.magenta.testing.domain.generators.EmployeeGenerator;
import org.mockito.Mockito;

import com.google.common.base.Supplier;

public class FixtureFactoryTest extends FixtureFactoryTestSupport {

  @Test
  public void testGenerator_a_list_of_a_specific_number_of_items() {

    // setup fixtures
    int NUMBER_OF_EMPLOYEES = 5;
    Supplier<Employee> gen = Mockito.spy(new EmployeeGenerator());

    FixtureFactory<SimpleDataSpecification> fixtures = createAnonymousFixtureFactory();
    fixtures.newGenerator(Employee.class)
        .generatedBy(gen, NUMBER_OF_EMPLOYEES + 10);
    fixtures.newDataSet(Occupation.class)
        .composedOf(Occupation.values());

    // exercise sut
    List<Employee> e = fixtures
        .restrictTo(Occupation.ENGINEER)
        .dataset(Employee.class)
        .list(NUMBER_OF_EMPLOYEES);

    // verify outcome
    assertThat(e).extracting("occupation")
        .containsExactly(Occupation.ENGINEER, Occupation.ENGINEER, Occupation.ENGINEER, Occupation.ENGINEER, Occupation.ENGINEER);
    Mockito.verify(gen, times(NUMBER_OF_EMPLOYEES))
        .get();
  }

  @Test
  public void testGenerator_a_list_of_undetermined_number_of_items() {

    // setup fixtures
    int NUMBER_OF_EMPLOYEES = 5;
    Supplier<Employee> gen = Mockito.spy(new EmployeeGenerator());

    FixtureFactory<SimpleDataSpecification> fixtures = createAnonymousFixtureFactory();
    fixtures.newGenerator(Employee.class)
        .generatedBy(gen, NUMBER_OF_EMPLOYEES);
    fixtures.newDataSet(Occupation.class)
        .composedOf(Occupation.values());

    // exercise sut
    List<Employee> e = fixtures.restrictTo(Occupation.ENGINEER)
        .dataset(Employee.class)
        .list();

    // verify outcome
    Mockito.verify(gen, times(NUMBER_OF_EMPLOYEES))
        .get();
  }
}
