package org.magenta.example.generators;

import java.awt.Color;

import org.magenta.DataDomain;
import org.magenta.SimpleGenerationStrategy;
import org.magenta.random.Randoms;

public class ColorGenerator implements SimpleGenerationStrategy<Color, ColorSpecification>{

  @Override
  public Color generateItem(DataDomain<? extends ColorSpecification> dataDomain) {

    Randoms randoms = dataDomain.getRandomizer();
    ColorSpecification spec = dataDomain.getSpecification();

    Integer red = randoms.integers(spec.getReds()).any();
    Integer green = randoms.integers(spec.getGreens()).any();
    Integer blue = randoms.integers(spec.getBlues()).any();

    Color color = new Color(red, green, blue);

    return color;

  }

  @Override
  public int getPreferredNumberOfItems(ColorSpecification specification) {
    return specification.getDefaultNumberOfItems();
  }

}
