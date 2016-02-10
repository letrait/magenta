package org.magenta;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.magenta.testing.domain.company.Employee;
import org.magenta.testing.domain.company.Occupation;
import org.magenta.testing.domain.company.PhoneNumber;

public class FixtureFactoryAutoMagicallyGenerateObjectTest {

  @Test
  public void auto_magically_generate_simple_object(){
    //setup fixture
    FixtureFactory sut = createRootFixtureFactory();

    //exercise sut
    sut.newDataSet(PhoneNumber.class).autoMagicallyGenerated(5);

    //verify outcome
    List<PhoneNumber> actual = sut.dataset(PhoneNumber.class).list();

    assertThat(actual).extracting("phoneNumber").hasSize(5).doesNotContainNull();

  }

  @Test
  public void auto_magically_generate_object_graph(){
    //setup fixture
    FixtureFactory sut = createRootFixtureFactory();

    //exercise sut
    sut.newDataSet(Employee.class).autoMagicallyGenerated(5);

    //verify outcome
    List<Employee> actual = sut.dataset(Employee.class).list();

    assertThat(actual).extracting("employeeId").hasSize(5).doesNotContainNull();
    assertThat(actual).extracting("name").hasSize(5).doesNotContainNull();
    assertThat(actual).extracting("occupation").hasSize(5).containsExactly(Occupation.TECHNICIAN,Occupation.ENGINEER,Occupation.MANAGEMENT, Occupation.TESTER, Occupation.TECHNICIAN);
    assertThat(actual).extracting("address").hasSize(5).doesNotContainNull();
    assertThat(actual).extracting("phoneNumbers").hasSize(5).doesNotContainNull();
  }

  @Test
  public void auto_magically_generate_set_for_attribtue(){
    //setup fixture
    FixtureFactory sut = createRootFixtureFactory();

    //exercise sut
    sut.newDataSet(Employee.class).autoMagicallyGenerated(1);

    //verify outcome
    Employee actual = sut.dataset(Employee.class).head();

    assertThat(actual.getPhoneNumbers()).isNotEmpty();
  }

  private FixtureFactory createRootFixtureFactory() {
    return Magenta.newFixture();
  }
}
