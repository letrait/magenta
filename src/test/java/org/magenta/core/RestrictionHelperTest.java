package org.magenta.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.Test;
import org.magenta.DataKey;
import org.magenta.DataSet;
import org.magenta.FixtureFactory;
import org.magenta.SimpleDataSpecification;
import org.magenta.random.FluentRandom;
import org.magenta.testing.domain.Employee;
import org.magenta.testing.domain.generators.EmployeeGenerator;

public class RestrictionHelperTest {

	@Test(expected = IllegalArgumentException.class)
	public void testApplyRestrictions_with_non_existing_dataset(){

		//setup fixtures
		FixtureFactory<SimpleDataSpecification> sut=createDataDomainManager();


		Employee candidate=createAnonymousEmployee("testApplyRestrictions_single_item");

		//exercise sut
		//Since the data domain does not contain any Employee dataset, it is illegal to
		//try to restrict on Employee
		RestrictionHelper.applyRestrictions(sut, candidate);


	}

	@Test
	public void testApplyRestrictions_single_item(){

		//setup fixtures
		FixtureFactory<SimpleDataSpecification> sut=createDataDomainManager();
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
		FixtureFactory<SimpleDataSpecification> sut=createDataDomainManager();
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
		FixtureFactory<SimpleDataSpecification> sut=createDataDomainManager();
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
		FixtureFactory<SimpleDataSpecification> sut=createDataDomainManager();
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
		FixtureFactory<SimpleDataSpecification> sut=createDataDomainManager();
		sut.newDataSet(Employee.class).generatedBy(new EmployeeGenerator());

		Employee candidate1=createAnonymousEmployee("candidate1");
		Employee candidate2=createAnonymousEmployee("candidate2");
		Employee candidate3=createAnonymousEmployee("candidate3");
		Employee candidate4=createAnonymousEmployee("candidate4");
		Employee candidate5=createAnonymousEmployee("candidate5");

		DataSet<Employee> employees = new GenericDataSet<>(Arrays.asList(candidate1,candidate2,candidate3,candidate4,candidate5), Employee.class, sut.getRandomizer());

		//exercise sut
		RestrictionHelper.applyRestrictions(sut, employees);

		//verify outcome
		assertThat(sut.dataset(Employee.class).list()).containsExactly(candidate1, candidate2, candidate3, candidate4, candidate5);
	}

	@Test
	public void testApplyRestrictions_using_a_qualified_dataset(){

		//setup fixtures
		FixtureFactory<SimpleDataSpecification> sut=createDataDomainManager();
		sut.newDataSet(Employee.class).generatedBy(new EmployeeGenerator());

		Employee candidate1=createAnonymousEmployee("candidate1");
		Employee candidate2=createAnonymousEmployee("candidate2");
		Employee candidate3=createAnonymousEmployee("candidate3");
		Employee candidate4=createAnonymousEmployee("candidate4");
		Employee candidate5=createAnonymousEmployee("candidate5");

		DataKey<Employee> key = DataKey.makeDefault(Employee.class);

		DataSet<Employee> employees = key.asDataSet(sut.getRandomizer(), candidate1,candidate2,candidate3,candidate4,candidate5);

		//exercise sut
		RestrictionHelper.applyRestrictions(sut, employees);

		//verify outcome
		assertThat(sut.dataset(Employee.class).list()).containsExactly(candidate1, candidate2, candidate3, candidate4, candidate5);
	}

	 @Test
	  public void testApplyRestrictions_using_an_empty_dataset(){

	    //setup fixtures
	    FixtureFactory<SimpleDataSpecification> sut=createDataDomainManager();
	    sut.newDataSet(Employee.class).generatedBy(new EmployeeGenerator());

	    DataKey<Employee> key = DataKey.makeDefault(Employee.class);

	    DataSet<Employee> employees = key.asEmptyDataSet();
	    //exercise sut
	    RestrictionHelper.applyRestrictions(sut, employees);

	    //verify outcome
	    assertThat(sut.dataset(Employee.class).list()).isEmpty();
	  }

	private Employee createAnonymousEmployee(String name) {
		Employee e=new Employee();
		e.setEmployeeId(1234L);
		e.setName(name);
		e.setOccupation("TESTER");
		return e;
	}

	private FixtureFactory<SimpleDataSpecification> createDataDomainManager() {
		return FixtureFactory.newRoot("RestrictionHelperTest",SimpleDataSpecification.create(),FluentRandom.singleton());
	}
}
