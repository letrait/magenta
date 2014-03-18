package org.magenta.example.generators;

import org.magenta.DataSpecification;

import com.google.common.collect.Range;

public interface ColorSpecification extends DataSpecification {

  public Range<Integer> getReds();
  public Range<Integer> getGreens();
  public Range<Integer> getBlues();

}
