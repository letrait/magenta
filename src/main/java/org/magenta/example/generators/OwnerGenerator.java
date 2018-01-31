package org.magenta.example.generators;

import java.util.function.Supplier;

import org.magenta.annotations.InjectFluentRandom;
import org.magenta.example.domain.Owner;
import org.magenta.random.FluentRandom;

public class OwnerGenerator implements Supplier<Owner> {

  @InjectFluentRandom
  private FluentRandom rnd;

  @Override
  public Owner get() {

    Owner owner = new Owner();
    owner.setName(rnd.strings().charabia(8));

    return owner;
  }

}
