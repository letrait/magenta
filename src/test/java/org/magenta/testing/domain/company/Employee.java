package org.magenta.testing.domain.company;

import java.util.Set;

import com.google.common.collect.Sets;

public class Employee {

  public static class Id{
    public long value;

    @Override
    public String toString(){
      return value+"";
    }

    public static Id value(long value){
      Id id= new Id();
      id.value = value;
      return id;
    }
  }

  private Id employeeId;
  private String name;
  private Occupation occupation;
  private Address address;
  private Set<PhoneNumber> phoneNumbers = Sets.newHashSet();

  public Id getEmployeeId() {
    return employeeId;
  }
  public void setEmployeeId(Id employeeId) {
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
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((address == null) ? 0 : address.hashCode());
    if(employeeId!=null){
      result = prime * result + (int) (employeeId.value ^ (employeeId.value >>> 32));
    }
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((occupation == null) ? 0 : occupation.hashCode());
    result = prime * result + ((phoneNumbers == null) ? 0 : phoneNumbers.hashCode());
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
    Employee other = (Employee) obj;
    if (address == null) {
      if (other.address != null)
        return false;
    } else if (!address.equals(other.address))
      return false;
    if (employeeId != other.employeeId)
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (occupation != other.occupation)
      return false;
    if (phoneNumbers == null) {
      if (other.phoneNumbers != null)
        return false;
    } else if (!phoneNumbers.equals(other.phoneNumbers))
      return false;
    return true;
  }








}
