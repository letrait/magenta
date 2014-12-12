package org.magenta.testing.domain.generators;

import org.magenta.DataSet;
import org.magenta.annotations.InjectDataSet;
import org.magenta.annotations.InjectFluentRandom;
import org.magenta.random.FluentRandom;
import org.magenta.testing.domain.Employee;
import org.magenta.testing.domain.Occupation;

import com.google.common.base.Supplier;

public class EmployeeGenerator implements Supplier<Employee>{

  @InjectFluentRandom
  private FluentRandom r;

  @InjectDataSet
  private DataSet<Occupation> occupations;

	@Override
	public Employee get() {

		Employee e=new Employee();
		e.setEmployeeId(r.longs().any());
		e.setName(r.strings().charabia(6));
		e.setOccupation(occupations.any());

		return e;
	}


}