package org.magenta.testing.domain.company;

import java.util.Date;

import com.google.common.collect.Range;

public class Contract {

	private Employee employee;
	private Range<Date> effective;
	
	
	public Employee getEmployee() {
		return employee;
	}
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	public Range<Date> getEffective() {
		return effective;
	}
	public void setEffective(Range<Date> effective) {
		this.effective = effective;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((effective == null) ? 0 : effective.hashCode());
		result = prime * result + ((employee == null) ? 0 : employee.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Contract other = (Contract) obj;
		if (effective == null) {
			if (other.effective != null)
				return false;
		} else if (!effective.equals(other.effective))
			return false;
		if (employee == null) {
			if (other.employee != null)
				return false;
		} else if (!employee.equals(other.employee))
			return false;
		return true;
	}
	
	
}
