package org.magenta.generators;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.magenta.DataDomain;
import org.magenta.DataKey;
import org.magenta.DataSpecification;
import org.magenta.SimpleGenerationStrategy;
import org.magenta.random.RandomBuilder;
import org.magenta.testing.domain.Employee;

import com.google.common.collect.Lists;

public class SimpleGenerationStrategyAdapterTest {

	private DataDomain<DataSpecification> dataDomain;
	private SimpleGenerationStrategy<Employee, DataSpecification> strategy;


	@Test
	public void testGenerate(){

		//setup fixtures

    int CONFIGURED_NUMBER_OF_ITEMS = 3;

    DataKey<String> key = DataKey.makeDefault(String.class);

    DataDomain<DataSpecification> dataDomain=mock(DataDomain.class);

    when(dataDomain.numberOfElementsFor(key)).thenReturn(CONFIGURED_NUMBER_OF_ITEMS);

    strategy=mock(SimpleGenerationStrategy.class);


		List<DataKey<?>> affectedDataSet = Lists.newArrayList();

		SimpleGenerationStrategyAdapter<Employee, DataSpecification> sut=new SimpleGenerationStrategyAdapter<>(key, strategy,affectedDataSet);

		Employee e1 = generateEmployee();
		Employee e2 = generateEmployee();
		Employee e3 = generateEmployee();

		when(strategy.generateItem(dataDomain)).thenReturn(e1, e2, e3);

		//exercise sut
		Iterable<Employee> actual = sut.generate(dataDomain);

		//verify outcome
		assertThat(actual).containsOnly(e1,e2,e3);

		verify(strategy,times(CONFIGURED_NUMBER_OF_ITEMS)).generateItem(dataDomain);
	}


	private Employee generateEmployee() {
		RandomBuilder r=RandomBuilder.PROVIDER.singleton();

		Employee e=new Employee();
		e.setEmployeeId(r.longs().any());
		e.setName(r.strings().charabia(6));
		e.setOccupation(r.array("TECHNICIAN","ENGINEER","MANAGEMENT").any());
		return e;
	}
}
