package org.magenta.example;


import org.magenta.example.generators.CarSpecification;
import org.magenta.example.generators.ColorSpecification;
import org.magenta.example.generators.TripSpecification;

import com.google.common.collect.Range;

public class ExampleDataSpecification implements ColorSpecification,CarSpecification, TripSpecification {

  private static Range<Integer> FULL_RANGE = Range.closed(0, 255);
  private static Range<Integer> EMPTY = Range.closed(0, 0);

  private Range<Integer> reds = FULL_RANGE;
  private Range<Integer> greens = FULL_RANGE;
  private Range<Integer> blues = FULL_RANGE;

  private Range<Integer> years = Range.closed(1996,2014);
  private Range<Integer> distanceInKm = Range.closed(10,400);
  private Range<Integer> numberOfTripsByCar = Range.closed(1,5);

  public ExampleDataSpecification(){

  }

  @Override
  public int getDefaultNumberOfItems() {
    return 5;
  }

  @Override
  public Range<Integer> getReds() {
    return reds;
  }

  @Override
  public Range<Integer> getGreens() {
    return greens;
  }

  @Override
  public Range<Integer> getBlues() {
    return blues;
  }

  public ExampleDataSpecification noReds() {
    this.reds = EMPTY;
    return this;
  }

  public ExampleDataSpecification noGreens() {
    this.greens = EMPTY;
    return this;
  }

  public ExampleDataSpecification noBlues() {
    this.blues = EMPTY;
    return this;
  }

  @Override
  public Range<Integer> getYearRange() {
    return this.years;
  }

  @Override
  public Range<Integer> getNumberOfTripsByCar() {
   return numberOfTripsByCar;
  }

  @Override
  public Range<Integer> getTripDistanceInKm() {
   return distanceInKm;
  }

}
