package org.magenta.example.generators;

import org.magenta.DataSpecification;

import com.google.common.collect.Range;

public interface TripSpecification extends DataSpecification {

  Range<Integer> getNumberOfTripsByCar();
  Range<Integer> getTripDistanceInKm();

}
