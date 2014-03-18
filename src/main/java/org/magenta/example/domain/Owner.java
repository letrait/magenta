package org.magenta.example.domain;

import java.util.HashSet;
import java.util.Set;

public class Owner {

  private String name;
  private Set<Car> cars = new HashSet<>();

  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  public Set<Car> getCars() {
    return cars;
  }

  public void setCars(Set<Car> car) {
    this.cars = car;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
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
    Owner other = (Owner) obj;

    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Owner [name=" + name + ", cars = ["+displayCars(cars)+"]";
  }

  private String displayCars(Set<Car> cars) {
    StringBuilder sb = new StringBuilder();
    for(Car c:cars){
      sb.append("[").append(c.getMaker()).append(" ").append(c.getYear()).append(" ").append(c.getColor()).append("],");
    }
    return sb.toString();
  }

}
