package org.magenta.example.generators;

import java.awt.Color;

import org.magenta.annotations.InjectDataSpecification;
import org.magenta.annotations.InjectRandomBuilder;
import org.magenta.random.RandomBuilder;

import com.google.common.base.Supplier;

public class ColorGenerator implements Supplier<Color>{

  @InjectDataSpecification
  Supplier<ColorSpecification> spec;

  @InjectRandomBuilder
  RandomBuilder randoms;

  @Override
  public Color get() {

    Integer red = randoms.integers(spec.get().getReds()).any();
    Integer green = randoms.integers(spec.get().getGreens()).any();
    Integer blue = randoms.integers(spec.get().getBlues()).any();

    Color color = new Color(red, green, blue);

    return color;

  }


}
