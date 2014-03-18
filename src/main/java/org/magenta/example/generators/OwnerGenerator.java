package org.magenta.example.generators;

import org.magenta.DataDomain;
import org.magenta.DataSpecification;
import org.magenta.SimpleGenerationStrategy;
import org.magenta.annotations.Key;
import org.magenta.annotations.TriggeredGeneration;
import org.magenta.example.domain.Car;
import org.magenta.example.domain.Owner;
import org.magenta.random.Randoms;

@TriggeredGeneration(@Key(Car.class))
public class OwnerGenerator implements SimpleGenerationStrategy<Owner, DataSpecification> {

  @Override
  public Owner generateItem(DataDomain<? extends DataSpecification> dataDomain) {

    Randoms rnd=dataDomain.getRandomizer();

    Owner owner = new Owner();
    owner.setName(rnd.strings().charabia(8));


    return owner;
  }

  @Override
  public int getPreferredNumberOfItems(DataSpecification specification) {
    return specification.getDefaultNumberOfItems();
  }

}
