package org.magenta.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.Test;
import org.magenta.DataKey;
import org.magenta.DataSet;
import org.magenta.FixtureFactory;
import org.magenta.Magenta;
import org.magenta.core.data.supplier.StaticDataSupplier;
import org.magenta.testing.domain.company.Employee;
import org.magenta.testing.domain.company.EmployeeGenerator;
import org.magenta.testing.domain.company.Occupation;

import com.google.common.reflect.TypeToken;


public class RestrictionHelperTest {

  @Test(expected = IllegalArgumentException.class)
  public void testApplyRestrictions_with_non_existing_dataset(){

    //setup fixtures
    FixtureFactory sut=createFixtureFactory();


    Employee candidate=createAnonymousEmployee("testApplyRestrictions_single_item");

    //exercise sut
    //Since the data domain does not contain any Employee dataset, it is illegal to
    //try to restrict on Employee
    RestrictionHelper.applyRestrictions(sut, candidate);


  }

  @Test
  public void testApplyRestrictions_single_item(){

    //setup fixtures
    FixtureFactory sut=createFixtureFactory();
    sut.newDataSet(Employee.class).generatedBy(new EmployeeGenerator());

    Employee candidate=createAnonymousEmployee("testApplyRestrictions_single_item");

    //exercise sut
    RestrictionHelper.applyRestrictions(sut, candidate);

    //verify outcome
    assertThat(sut.dataset(Employee.class).list()).containsExactly(candidate);
  }

  @Test
  public void testApplyRestrictions_item_lists(){

    //setup fixtures
    FixtureFactory sut=createFixtureFactory();
    sut.newDataSet(Employee.class).generatedBy(new EmployeeGenerator());

    Employee candidate1=createAnonymousEmployee("candidate1");
    Employee candidate2=createAnonymousEmployee("candidate2");
    Employee candidate3=createAnonymousEmployee("candidate3");

    //exercise sut
    RestrictionHelper.applyRestrictions(sut, Arrays.asList(candidate1,candidate2,candidate3));

    //verify outcome
    assertThat(sut.dataset(Employee.class).list()).containsExactly(candidate1,candidate2,candidate3);
  }

  @Test
  public void testApplyRestrictions_item_array(){

    //setup fixtures
    FixtureFactory sut=createFixtureFactory();
    sut.newDataSet(Employee.class).generatedBy(new EmployeeGenerator());

    Employee candidate1=createAnonymousEmployee("candidate1");
    Employee candidate2=createAnonymousEmployee("candidate2");
    Employee candidate3=createAnonymousEmployee("candidate3");

    //exercise sut
    RestrictionHelper.applyRestrictions(sut, candidate1, new Employee[]{candidate2,candidate3});

    //verify outcome
    assertThat(sut.dataset(Employee.class).list()).containsExactly(candidate1,candidate2,candidate3);
  }

  @Test
  public void testApplyRestrictions_mix_of_array_and_list(){

    //setup fixtures
    FixtureFactory sut=createFixtureFactory();
    sut.newDataSet(Employee.class).generatedBy(new EmployeeGenerator());

    Employee candidate1=createAnonymousEmployee("candidate1");
    Employee candidate2=createAnonymousEmployee("candidate2");
    Employee candidate3=createAnonymousEmployee("candidate3");
    Employee candidate4=createAnonymousEmployee("candidate4");
    Employee candidate5=createAnonymousEmployee("candidate5");

    //exercise sut
    RestrictionHelper.applyRestrictions(sut, candidate1, new Employee[]{candidate2,candidate3},Arrays.asList(candidate4,candidate5));

    //verify outcome
    assertThat(sut.dataset(Employee.class)
        .list()).containsExactly(candidate1, candidate2, candidate3, candidate4, candidate5);
  }

  @Test
  public void testApplyRestrictions_using_a_dataset(){

    //setup fixtures
    FixtureFactory sut=createFixtureFactory();
    sut.newDataSet(Employee.class).generatedBy(new EmployeeGenerator());

    Employee candidate1=createAnonymousEmployee("candidate1");
    Employee candidate2=createAnonymousEmployee("candidate2");
    Employee candidate3=createAnonymousEmployee("candidate3");
    Employee candidate4=createAnonymousEmployee("candidate4");
    Employee candidate5=createAnonymousEmployee("candidate5");

    DataSet<Employee> employees = new DataSetImpl<>(new StaticDataSupplier<>(Arrays.asList(candidate1,candidate2,candidate3,candidate4,candidate5), TypeToken.of(Employee.class)));

    //exercise sut
    RestrictionHelper.applyRestrictions(sut, employees);

    //verify outcome
    assertThat(sut.dataset(Employee.class).list()).containsExactly(candidate1, candidate2, candidate3, candidate4, candidate5);
  }

  @Test
  public void testApplyRestrictions_fixture_datakeys_order_follow_restriction_order(){
    //setup fixtures
    FixtureFactory parent=createFixtureFactory();

    parent.newDataSet(Integer.class).composedOf(1,2,3,4,5);
    parent.newDataSet(String.class).composedOf("a","b","c","d");

    FixtureFactory sut = parent.newChild();

    //exercise sut
    RestrictionHelper.applyRestrictions(sut, 9, "z");

    //verify outcome
    assertThat(sut.keys()).containsExactly(DataKey.of(Integer.class), DataKey.of(String.class));

  }

  @Test
  public void testApplyRestrictions_fixture_datakeys_order_follow_restriction_order_opposite_case(){
    //setup fixtures
    FixtureFactory parent=createFixtureFactory();

    parent.newDataSet(Integer.class).composedOf(1,2,3,4,5);
    parent.newDataSet(String.class).composedOf("a","b","c","d");

    FixtureFactory sut = parent.newChild();

    //exercise sut
    RestrictionHelper.applyRestrictions(sut, "z", 9);

    //verify outcome
    assertThat(sut.keys()).containsExactly( DataKey.of(String.class), DataKey.of(Integer.class));

  }

  /*@Test
	public void testApplyRestrictions_using_a_qualified_dataset(){

		//setup fixtures
		FixtureFactory sut=createFixtureFactory();
		sut.newDataSet(Employee.class).generatedBy(new EmployeeGenerator());

		Employee candidate1=createAnonymousEmployee("candidate1");
		Employee candidate2=createAnonymousEmployee("candidate2");
		Employee candidate3=createAnonymousEmployee("candidate3");
		Employee candidate4=createAnonymousEmployee("candidate4");
		Employee candidate5=createAnonymousEmployee("candidate5");

		DataKey<Employee> key = DataKey.of(Employee.class);

		DataSet<Employee> employees = key.datasetOf(sut.getFluentRandom(), candidate1,candidate2,candidate3,candidate4,candidate5);

		//exercise sut
		RestrictionHelper.applyRestrictions(sut, employees);

		//verify outcome
		assertThat(sut.dataset(Employee.class).list()).containsExactly(candidate1, candidate2, candidate3, candidate4, candidate5);
	}*/

  /*@Test
	  public void testApplyRestrictions_using_an_empty_dataset(){

	    //setup fixtures
	    FixtureFactory sut=createFixtureFactory();
	    sut.newDataSet(Employee.class).generatedBy(new EmployeeGenerator());

	    DataKey<Employee> key = DataKey.makeDefault(Employee.class);

	    DataSet<Employee> employees = key.asEmptyDataSet();
	    //exercise sut
	    RestrictionHelper.applyRestrictions(sut, employees);

	    //verify outcome
	    assertThat(sut.dataset(Employee.class).list()).isEmpty();
	  }*/

  private Employee createAnonymousEmployee(String name) {
    Employee e=new Employee();
    e.setEmployeeId(Employee.Id.value(1234L));
    e.setName(name);
    e.setOccupation(Occupation.TESTER);
    return e;
  }

  private FixtureFactory createFixtureFactory() {
    return Magenta.newFixture();
  }
}
