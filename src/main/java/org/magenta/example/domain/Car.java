package org.magenta.example.domain;

import java.awt.Color;

public class Car {

  public enum Maker{
    FORD,
    CHEVROLET,
    TOYOTA,
    HONDA,
    SUBARU,
    HYUNDAI;
  }

  private Color color;
  private Maker maker;
  private int Year;
  private Owner owner;

  public Color getColor() {
    return color;
  }
  public void setColor(Color color) {
    this.color = color;
  }
  public Maker getMaker() {
    return maker;
  }
  public void setMaker(Maker maker) {
    this.maker = maker;
  }
  public int getYear() {
    return Year;
  }
  public void setYear(int year) {
    Year = year;
  }

  public Owner getOwner() {
    return owner;
  }
  public void setOwner(Owner owner) {
    this.owner = owner;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Year;
    result = prime * result + ((color == null) ? 0 : color.hashCode());
    result = prime * result + ((maker == null) ? 0 : maker.hashCode());
    result = prime * result + ((owner == null) ? 0 : owner.hashCode());
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
    Car other = (Car) obj;
    if (Year != other.Year)
      return false;
    if (color == null) {
      if (other.color != null)
        return false;
    } else if (!color.equals(other.color))
      return false;
    if (maker != other.maker)
      return false;
    if (owner == null) {
      if (other.owner != null)
        return false;
    } else if (!owner.equals(other.owner))
      return false;
    return true;
  }
  @Override
  public String toString() {
    return "Car [color=" + color + ", maker=" + maker + ", Year=" + Year + ", owner=" + owner + "]";
  }


}
