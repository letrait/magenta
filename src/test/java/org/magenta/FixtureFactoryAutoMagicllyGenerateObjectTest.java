package org.magenta;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.magenta.testing.domain.company.Employee;
import org.magenta.testing.domain.company.PhoneNumber;

public class FixtureFactoryAutoMagicllyGenerateObjectTest {

  @Test
  public void auto_magically_generate_simple_object(){
    //setup fixture
    FixtureFactory sut = createRootFixtureFactory();

    //exercise sut
    sut.newDataSet(PhoneNumber.class).autoMagicallyGenerated(5);

    //verify outcome
    List<PhoneNumber> actual = sut.dataset(PhoneNumber.class).list();

    System.out.println(actual);

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

    System.out.println(actual);

    assertThat(actual).extracting("employeeId").hasSize(5).doesNotContainNull();

  }

  private FixtureFactory createRootFixtureFactory() {
    return Magenta.newFixture();
  }
}
