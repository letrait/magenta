package org.magenta.example.generators;

import org.magenta.DataSpecification;

import com.google.common.collect.Range;

public interface CarSpecification extends DataSpecification{

  Range<Integer> getYearRange();

}
