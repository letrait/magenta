package org.magenta.testing.domain.generators;

import org.magenta.annotations.InjectFluentRandom;
import org.magenta.random.FluentRandom;
import org.magenta.testing.domain.Employee;

import com.google.common.base.Supplier;

public class EmployeeGenerator implements Supplier<Employee>{

  @InjectFluentRandom
  FluentRandom r;

	@Override
	public Employee get() {

		Employee e=new Employee();
		e.setEmployeeId(r.longs().any());
		e.setName(r.strings().charabia(6));
		e.setOccupation(r.array("TECHNICIAN","ENGINEER","MANAGEMENT").any());

		return e;
	}


}