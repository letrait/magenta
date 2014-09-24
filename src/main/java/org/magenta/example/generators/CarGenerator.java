package org.magenta.example.generators;

import java.awt.Color;

import org.magenta.DataSet;
import org.magenta.annotations.InjectDataSet;
import org.magenta.annotations.InjectDataSpecification;
import org.magenta.annotations.InjectRandomBuilder;
import org.magenta.example.domain.Car;
import org.magenta.example.domain.Owner;
import org.magenta.random.RandomBuilder;

import com.google.common.base.Supplier;

public class CarGenerator implements Supplier<Car> {

  @InjectDataSet
  private DataSet<Color> colors;

  @InjectDataSet
  private DataSet<Car.Maker> makers;

  @InjectDataSet
  private DataSet<Owner> owners;

  @InjectRandomBuilder
  private RandomBuilder rnd;

  @InjectDataSpecification
  private Supplier<CarSpecification> spec;

  @Override
  public Car get() {

    Car car = new Car();
    car.setColor(colors.any());
    car.setMaker(makers.any());
    car.setYear(rnd.integers().any(spec.get().getYearRange()));

    if(!owners.isEmpty()) {
      car.setOwner(owners.any());

      //add the car to the owner's car list
      car.getOwner().getCars().add(car);
    }

    return car;
  }




}
