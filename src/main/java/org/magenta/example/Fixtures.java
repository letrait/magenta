package org.magenta.example;

import java.awt.Color;

import org.magenta.Fixture;
import org.magenta.FixtureFactory;
import org.magenta.SimpleDataSpecification;
import org.magenta.example.domain.Car;
import org.magenta.example.domain.Owner;
import org.magenta.example.generators.CarGenerator;
import org.magenta.example.generators.ColorGenerator;
import org.magenta.example.generators.OwnerGenerator;
import org.magenta.random.FluentRandom;

public class Fixtures {

  public static Fixture<SimpleDataSpecification> rgb() {

    FixtureFactory<SimpleDataSpecification> domain = FixtureFactory.newRoot("colors", SimpleDataSpecification.create(),FluentRandom.singleton());
    domain.newDataSet(Color.class).composedOf(Color.RED, Color.GREEN, Color.BLUE);
    return domain;

  }

  public static Fixture<ExampleDataSpecification> multicolor() {

    FixtureFactory<ExampleDataSpecification> domain = FixtureFactory.newRoot("colors", new ExampleDataSpecification(),FluentRandom.singleton());
    domain.newDataSet(Color.class)
        .generatedBy(new ColorGenerator());
    return domain;

  }


  public static FixtureFactory<ExampleDataSpecification> automotives() {
    FixtureFactory<ExampleDataSpecification> domain = FixtureFactory.newRoot("colors", new ExampleDataSpecification(),FluentRandom.singleton());
    domain.newDataSet(Color.class).generatedBy(new ColorGenerator(),10);
    domain.newDataSet(Car.class).generatedBy(new CarGenerator(),20);
    domain.newDataSet(Car.Maker.class).composedOf(Car.Maker.values());
    domain.newDataSet(Owner.class).generatedBy(new OwnerGenerator(),3);

    return domain;
  }
}
