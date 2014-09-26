package org.magenta.example.generators;



import java.util.List;
import java.util.concurrent.TimeUnit;

import org.magenta.DataSet;
import org.magenta.annotations.InjectDataSet;
import org.magenta.annotations.InjectDataSpecification;
import org.magenta.annotations.InjectFluentRandom;
import org.magenta.example.domain.Car;
import org.magenta.example.domain.Trip;
import org.magenta.random.FluentRandom;

import com.google.common.base.Supplier;
import com.google.inject.internal.Lists;

public class TripGenerator implements Supplier<Iterable<Trip>> {

  @InjectFluentRandom
  private FluentRandom rnd;

  @InjectDataSpecification
  private TripSpecification spec;

  @InjectDataSet
  private DataSet<Car> cars;

  @Override
  public Iterable<Trip> get() {

    List<Trip> trips = Lists.newArrayList();

    for(Car car:cars.list()){

      Integer numberOfTrips = rnd.integers(spec.getNumberOfTripsByCar()).any();

      for(int i = 0 ; i< numberOfTrips; i++){

        Integer distanceInKm = rnd.integers(spec.getTripDistanceInKm()).any();

        Trip trip = new Trip();
        trip.setCar(car);
        trip.setDistanceInKm(distanceInKm);
        trip.setDate(rnd.dates().anyInThePast(30, TimeUnit.DAYS));
        trips.add(trip);
      }
    }

    return trips;

  }


}

