package org.magenta.testing.domain.company;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.assertj.core.util.Lists;
import org.magenta.DataSet;
import org.magenta.annotation.InjectDataSet;
import org.magenta.random.FluentRandom;

import com.google.common.base.Supplier;
import com.google.common.collect.Range;

public class ContractGenerator implements Supplier<Iterable<Contract>> {

	@InjectDataSet
	private DataSet<Employee> employees;

	@Override
	public Iterable<Contract> get() {

		List<Contract> contracts = Lists.newArrayList();

		for (Employee e : employees) {
			Contract c = new Contract();

			Date from = FluentRandom.dates().any();
			Date to = FluentRandom.dates().anyInTheNext(from, 365 * 3, TimeUnit.DAYS);
			c.setEffective(Range.open(from, to));
			c.setEmployee(e);

			contracts.add(c);
		}

		return contracts;
	}

}
