package org.magenta.example.generators;

import java.awt.Color;

import org.magenta.DataSet;
import org.magenta.annotations.InjectDataSet;
import org.magenta.annotations.InjectDataSpecification;
import org.magenta.annotations.InjectFluentRandom;
import org.magenta.example.domain.Car;
import org.magenta.example.domain.Owner;
import org.magenta.random.FluentRandom;

import com.google.common.base.Supplier;

public class CarGenerator implements Supplier<Car> {

  @InjectDataSet
  private DataSet<Color> colors;

  @InjectDataSet
  private DataSet<Car.Maker> makers;

  @InjectDataSet(modified = true)
  private DataSet<Owner> owners;

  @InjectFluentRandom
  private FluentRandom rnd;

  @InjectDataSpecification
  private CarSpecification spec;

  @Override
  public Car get() {

    Car car = new Car();
    car.setColor(colors.any());
    car.setMaker(makers.any());
    car.setYear(rnd.integers().any(spec.getYearRange()));

    if(!owners.isEmpty()) {
      car.setOwner(owners.any());

      //add the car to the owner's car list
      car.getOwner().getCars().add(car);
    }

    return car;
  }




}
