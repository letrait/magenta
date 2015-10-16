package org.magenta;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.magenta.testing.domain.Address;
import org.magenta.testing.domain.AddressGenerator;
import org.magenta.testing.domain.Employee;
import org.magenta.testing.domain.EmployeeGenerator2;
import org.magenta.testing.domain.Occupation;

public class FixtureFactoryHierarchyUseCaseTest {

  private FixtureFactory fixtures;

  @Before
  public void setupFixtures(){
    FixtureFactory fixtures = Magenta.newFixture();
    fixtures.newDataSet(Occupation.class).composedOf(Occupation.ENGINEER, Occupation.MANAGEMENT, Occupation.TECHNICIAN, Occupation.TESTER);
    fixtures.newGenerator(Address.class).generatedBy(new AddressGenerator());
    fixtures.newDataSet(Employee.class).generatedBy(new EmployeeGenerator2());
    this.fixtures = fixtures;

  }

  @Test
  public void testSequencesAreIndependantFromAFixtureToAnother_each_fixture_have_their_own_sequence_starting_from_the_same_value(){

    //setup fixtures
    FixtureFactory parent = fixtures;
    FixtureFactory child1=fixtures.newChild();
    FixtureFactory child2=fixtures.newChild();
    FixtureFactory grandChild=child1.newChild();

    //exercise sut
   Employee engineer = parent.dataset(Employee.class).get(0);
   Employee engineerFromChild1 = child1.dataset(Employee.class).get(0);
   Employee engineerFromChild2 = child2.dataset(Employee.class).get(0);
   Employee engineerFromGrandChild = grandChild.dataset(Employee.class).get(0);

    //verify outcome
    assertThat(engineer).isNotSameAs(engineerFromChild1).isNotSameAs(engineerFromChild2).isNotSameAs(engineerFromGrandChild);
    assertThat(engineer.getOccupation())
    .isEqualTo(engineerFromChild1.getOccupation())
    .isEqualTo(engineerFromChild2.getOccupation())
    .isEqualTo(engineerFromGrandChild.getOccupation());
  }

  @Test
  public void testSequencesAreIndependantFromAFixtureToAnother_test_alterning_get_should_not_change_sequence(){

    //setup fixtures
    FixtureFactory parent = fixtures;
    FixtureFactory child1=fixtures.newChild();


    //exercise sut
   Employee engineer = parent.dataset(Employee.class).get(0);
   Employee engineerFromChild1 = child1.dataset(Employee.class).get(0);
   Employee manager = parent.dataset(Employee.class).get(1);
   Employee managerFromChild1 = child1.dataset(Employee.class).get(1);

    //verify outcome
    assertThat(engineer).isNotSameAs(engineerFromChild1);
    assertThat(engineer.getOccupation())
    .isEqualTo(engineerFromChild1.getOccupation())
    .isEqualTo(Occupation.ENGINEER);
    assertThat(manager.getOccupation()).isEqualTo(managerFromChild1.getOccupation()).isEqualTo(Occupation.MANAGEMENT);
  }






}
