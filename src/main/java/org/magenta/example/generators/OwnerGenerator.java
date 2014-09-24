package org.magenta.example.generators;

import org.magenta.annotations.InjectRandomBuilder;
import org.magenta.annotations.Key;
import org.magenta.annotations.TriggeredGeneration;
import org.magenta.example.domain.Car;
import org.magenta.example.domain.Owner;
import org.magenta.random.RandomBuilder;

import com.google.common.base.Supplier;

@TriggeredGeneration(@Key(Car.class))
public class OwnerGenerator implements Supplier<Owner> {

  @InjectRandomBuilder
  private RandomBuilder rnd;

  @Override
  public Owner get() {

    Owner owner = new Owner();
    owner.setName(rnd.strings().charabia(8));

    return owner;
  }

}
