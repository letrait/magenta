package org.magenta.example;

import java.awt.Color;

import org.magenta.DataDomain;
import org.magenta.DataDomainManager;
import org.magenta.SimpleDataSpecification;
import org.magenta.example.domain.Car;
import org.magenta.example.domain.Owner;
import org.magenta.example.generators.CarGenerator;
import org.magenta.example.generators.ColorGenerator;
import org.magenta.example.generators.OwnerGenerator;
import org.magenta.random.RandomBuilder;

public class Fixtures {

  public static DataDomain<SimpleDataSpecification> rgb() {

    DataDomainManager<SimpleDataSpecification> domain = DataDomainManager.newRoot("colors", SimpleDataSpecification.create(),RandomBuilder.PROVIDER.singleton());
    domain.newDataSet(Color.class).composedOf(Color.RED, Color.GREEN, Color.BLUE);
    return domain;

  }

  public static DataDomain<ExampleDataSpecification> multicolor() {

    DataDomainManager<ExampleDataSpecification> domain = DataDomainManager.newRoot("colors", new ExampleDataSpecification(),RandomBuilder.PROVIDER.singleton());
    domain.newDataSet(Color.class)
        .generatedBy(new ColorGenerator());
    return domain;

  }


  public static DataDomainManager<ExampleDataSpecification> automotives() {
    DataDomainManager<ExampleDataSpecification> domain = DataDomainManager.newRoot("colors", new ExampleDataSpecification(),RandomBuilder.PROVIDER.singleton());
    domain.newDataSet(Color.class).generatedBy(new ColorGenerator());
    domain.newDataSet(Car.class).generatedBy(new CarGenerator());
    domain.newDataSet(Car.Maker.class).composedOf(Car.Maker.values());
    domain.newDataSet(Owner.class).generatedBy(new OwnerGenerator());

    return domain;
  }
}
