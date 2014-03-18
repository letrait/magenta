package org.magenta.generators;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.magenta.DataDomain;
import org.magenta.DataKey;
import org.magenta.DataSpecification;
import org.magenta.SimpleDataSpecification;
import org.magenta.SimpleGenerationStrategy;
import org.magenta.random.Randoms;
import org.magenta.testing.domain.Employee;

import com.google.common.collect.Lists;

public class SimpleGenerationStrategyAdapterTest {

	private DataDomain<DataSpecification> dataDomain;
	private SimpleGenerationStrategy<Employee, DataSpecification> strategy;


	@Before
	public void setup(){
		dataDomain=mock(DataDomain.class);
		when(dataDomain.getSpecification()).thenReturn(SimpleDataSpecification.create());
		strategy=mock(SimpleGenerationStrategy.class);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testConstructor_with_invalid_number(){

		//setup fixtures
		List<DataKey<?>> affectedDataSet = Lists.newArrayList();
		SimpleGenerationStrategy<Employee, DataSpecification> strategy=mock(SimpleGenerationStrategy.class);

		//exercise sut
		SimpleGenerationStrategyAdapter<Employee, DataSpecification> sut=new SimpleGenerationStrategyAdapter<>(strategy,-1,affectedDataSet);

	}

	@Test
	public void testGenerate_a_specific_number_of_items(){

		//setup fixtures
		final int SPECIFIED_NUMBER_OF_EMPLOYEES=2;

		List<DataKey<?>> affectedDataSet = Lists.newArrayList();

		SimpleGenerationStrategyAdapter<Employee, DataSpecification> sut=new SimpleGenerationStrategyAdapter<>(strategy,3,affectedDataSet);

		Employee e1 = generateEmployee();
		Employee e2 = generateEmployee();

		when(strategy.generateItem(dataDomain)).thenReturn(e1, e2);

		//exercise sut
		Iterable<Employee> actual = sut.generate(2, dataDomain);

		//verify outcome
		assertThat(actual).containsOnly(e1,e2);

		verify(strategy,times(SPECIFIED_NUMBER_OF_EMPLOYEES)).generateItem(dataDomain);
	}

	@Test
	public void testGenerate_should_use_configured_number_of_items_when_the_number_of_items_to_generate_is_not_specified(){

		//setup fixtures
		final int CONFIGURED_NUMBER_OF_EMPLOYEES=3;

		List<DataKey<?>> affectedDataSet = Lists.newArrayList();

		SimpleGenerationStrategyAdapter<Employee, DataSpecification> sut=new SimpleGenerationStrategyAdapter<>(strategy,CONFIGURED_NUMBER_OF_EMPLOYEES,affectedDataSet);

		Employee e1 = generateEmployee();
		Employee e2 = generateEmployee();
		Employee e3 = generateEmployee();

		when(strategy.generateItem(dataDomain)).thenReturn(e1, e2, e3);

		//exercise sut
		Iterable<Employee> actual = sut.generate(dataDomain);

		//verify outcome
		assertThat(actual).containsOnly(e1,e2,e3);

		verify(strategy,times(CONFIGURED_NUMBER_OF_EMPLOYEES)).generateItem(dataDomain);
	}

	@Test
	public void testGenerate_should_use_strategy_preferred_number_of_items_when_no_others_values_are_provided(){

		//setup fixtures
		final int STRATEGY_PREFERRED_NUMBER_OF_EMPLOYEES=3;

		List<DataKey<?>> affectedDataSet = Lists.newArrayList();

		SimpleGenerationStrategyAdapter<Employee, DataSpecification> sut=new SimpleGenerationStrategyAdapter<>(strategy,affectedDataSet);

		Employee e1 = generateEmployee();
		Employee e2 = generateEmployee();
		Employee e3 = generateEmployee();

		when(strategy.generateItem(dataDomain)).thenReturn(e1, e2, e3);
		when(strategy.getPreferredNumberOfItems(dataDomain.getSpecification())).thenReturn(STRATEGY_PREFERRED_NUMBER_OF_EMPLOYEES);

		//exercise sut
		Iterable<Employee> actual = sut.generate(dataDomain);

		//verify outcome
		assertThat(actual).containsOnly(e1,e2,e3);

		verify(strategy,times(STRATEGY_PREFERRED_NUMBER_OF_EMPLOYEES)).generateItem(dataDomain);
		verify(strategy).getPreferredNumberOfItems(dataDomain.getSpecification());
	}

	private Employee generateEmployee() {
		Randoms r=Randoms.singleton();

		Employee e=new Employee();
		e.setEmployeeId(r.longs().any());
		e.setName(r.strings().charabia(6));
		e.setOccupation(r.array("TECHNICIAN","ENGINEER","MANAGEMENT").any());
		return e;
	}
}
