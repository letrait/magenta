package org.magenta.example.domain;

import java.util.Date;

public class Trip {

  private Date date;
  private Car car;
  private int distanceInKm;

  public Date getDate() {
    return date;
  }
  public void setDate(Date date) {
    this.date = date;
  }
  public Car getCar() {
    return car;
  }
  public void setCar(Car car) {
    this.car = car;
  }
  public int getDistanceInKm() {
    return distanceInKm;
  }
  public void setDistanceInKm(int distanceInKm) {
    this.distanceInKm = distanceInKm;
  }
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((car == null) ? 0 : car.hashCode());
    result = prime * result + ((date == null) ? 0 : date.hashCode());
    result = prime * result + distanceInKm;
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
    Trip other = (Trip) obj;
    if (car == null) {
      if (other.car != null)
        return false;
    } else if (!car.equals(other.car))
      return false;
    if (date == null) {
      if (other.date != null)
        return false;
    } else if (!date.equals(other.date))
      return false;
    if (distanceInKm != other.distanceInKm)
      return false;
    return true;
  }
  @Override
  public String toString() {
    return "Trip [date=" + date + ", car=" + car + ", distanceInKm=" + distanceInKm + "]";
  }




}
