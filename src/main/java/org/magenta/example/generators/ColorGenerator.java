package org.magenta.example.generators;

import java.awt.Color;
import java.util.function.Supplier;

import org.magenta.annotations.InjectDataSpecification;
import org.magenta.annotations.InjectFluentRandom;
import org.magenta.random.FluentRandom;


public class ColorGenerator implements Supplier<Color>{

  @InjectDataSpecification
  ColorSpecification spec;

  @InjectFluentRandom
  FluentRandom randoms;

  @Override
  public Color get() {

    Integer red = randoms.integers(spec.getReds()).any();
    Integer green = randoms.integers(spec.getGreens()).any();
    Integer blue = randoms.integers(spec.getBlues()).any();

    Color color = new Color(red, green, blue);

    return color;

  }


}
