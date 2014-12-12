package org.magenta.testing.domain;

public class Employee {

	private long employeeId;
	private String name;
	private Occupation occupation;

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
  public void setOccupation(Occupation occupation) {
    this.occupation = occupation;
  }
  @Override
  public String toString() {
    return "Employee [employeeId=" + employeeId + ", name=" + name + ", occupation=" + occupation + "]";
  }




}
