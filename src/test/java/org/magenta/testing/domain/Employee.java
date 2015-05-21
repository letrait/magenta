package org.magenta.testing.domain;

import java.util.Set;

import com.google.common.collect.Sets;

public class Employee {

	private long employeeId;
	private String name;
	private Occupation occupation;
	private Address address;
	private Set<PhoneNumber> phoneNumbers = Sets.newHashSet();;

  public long getEmployeeId() {
    return employeeId;
  }
  public void setEmployeeId(long employeeId) {
    this.employeeId = employeeId;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public Occupation getOccupation() {
    return occupation;
  }



  /**
   * @return the address
   */
  public Address getAddress() {
    return address;
  }
  /**
   * @param address the address to set
   */
  public void setAddress(Address address) {
    this.address = address;
  }
  public void setOccupation(Occupation occupation) {
    this.occupation = occupation;
  }

  public Set<PhoneNumber> getPhoneNumbers(){
    return phoneNumbers;
  }
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Employee [employeeId=" + employeeId + ", name=" + name + ", occupation=" + occupation + ", address=" + address + ", phoneNumbers="
        + phoneNumbers + "]";
  }






}
