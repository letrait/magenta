package org.magenta.example.generators;



import java.util.List;
import java.util.concurrent.TimeUnit;

import org.magenta.DataDomain;
import org.magenta.ImplicitGenerationStrategy;
import org.magenta.example.domain.Car;
import org.magenta.example.domain.Trip;
import org.magenta.random.Randoms;

import com.google.inject.internal.Lists;

public class TripGenerator implements ImplicitGenerationStrategy<Trip, TripSpecification> {

  @Override
  public Iterable<Trip> generate(DataDomain<? extends TripSpecification> domain) {

    List<Trip> trips = Lists.newArrayList();
    Randoms rnd = domain.getRandomizer();

    for(Car car:domain.dataset(Car.class).list()){

      Integer numberOfTrips = rnd.integers(domain.getSpecification().getNumberOfTripsByCar()).any();

      for(int i = 0 ; i< numberOfTrips; i++){

        Integer distanceInKm = rnd.integers(domain.getSpecification().getTripDistanceInKm()).any();

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

