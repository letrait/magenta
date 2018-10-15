package org.magenta;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.magenta.testing.domain.company.Address;
import org.magenta.testing.domain.company.AddressGenerator;
import org.magenta.testing.domain.company.Employee;
import org.magenta.testing.domain.company.EmployeeGenerator2;
import org.magenta.testing.domain.company.Occupation;

public class FixtureFactoryCommonUseCaseTest {

  private FixtureFactory fixtures;

  @Before
  public void setupFixtures(){
    FixtureFactory fixtures = Magenta.newFixture();
    fixtures.newDataSet(Occupation.class).composedOf(Occupation.ENGINEER, Occupation.MANAGEMENT, Occupation.TECHNICIAN, Occupation.TESTER);
    fixtures.newDataSet(Address.class).generatedBy(new AddressGenerator());
    fixtures.newDataSet(Employee.class).generatedBy(new EmployeeGenerator2());
    this.fixtures = fixtures;

  }

  @Test
  public void test(){

    List<Employee> data = fixtures.dataset(Employee.class).list();

    //System.out.println(data);

    data.forEach(a->System.out.println(a));

  }
}
