package org.magenta.testing.domain;

import org.magenta.DataSet;
import org.magenta.Sequence;
import org.magenta.annotation.InjectDataSet;
import org.magenta.annotation.InjectSequence;

import com.google.common.base.Supplier;

public class EmployeeGenerator3 implements Supplier<Employee> {

  @InjectSequence
  private Sequence<Occupation> occupations;

  @InjectSequence
  private Sequence<Address> address;

  @InjectDataSet
  private DataSet<PhoneNumber> phoneNumbers;

  @Override
  public Employee get() {
    Employee e = new Employee();
    e.setOccupation(occupations.get());
    e.setAddress(address.get());
    e.getPhoneNumbers().addAll(phoneNumbers.set(2));
    return e;
  }

}
