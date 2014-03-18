package org.magenta.testing.domain.generators;

import org.magenta.DataDomain;
import org.magenta.DataSpecification;
import org.magenta.SimpleGenerationStrategy;
import org.magenta.random.Randoms;
import org.magenta.testing.domain.Employee;

public class EmployeeGenerator implements SimpleGenerationStrategy<Employee, DataSpecification>{

	@Override
	public Employee generateItem(DataDomain<? extends DataSpecification> datasets) {

		Randoms r=datasets.getRandomizer();

		Employee e=new Employee();
		e.setEmployeeId(r.longs().any());
		e.setName(r.strings().charabia(6));
		e.setOccupation(r.array("TECHNICIAN","ENGINEER","MANAGEMENT").any());

		return e;
	}

	@Override
	public int getPreferredNumberOfItems(DataSpecification specification) {
		return specification.getDefaultNumberOfItems();
	}

}