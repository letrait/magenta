package org.magenta.example.generators;

import java.awt.Color;

import org.magenta.DataDomain;
import org.magenta.DataSet;
import org.magenta.SimpleGenerationStrategy;
import org.magenta.example.domain.Car;
import org.magenta.example.domain.Owner;
import org.magenta.random.Randoms;

public class CarGenerator implements SimpleGenerationStrategy<Car, CarSpecification> {

  @Override
  public Car generateItem(DataDomain<? extends CarSpecification> dataDomain) {

    Randoms rnd=dataDomain.getRandomizer();
    CarSpecification spec = dataDomain.getSpecification();

    Car car = new Car();
    car.setColor(dataDomain.dataset(Color.class).any());
    car.setMaker(dataDomain.dataset(Car.Maker.class).any());
    car.setYear(rnd.integers().any(spec.getYearRange()));

    DataSet<Owner> owners = dataDomain.dataset(Owner.class);

    if(!owners.isEmpty()) {
      car.setOwner(owners.any());

      //add the car to the owner's car list
      car.getOwner().getCars().add(car);
    }

    return car;
  }

  @Override
  public int getPreferredNumberOfItems(CarSpecification specification) {
    return specification.getDefaultNumberOfItems();
  }

}
