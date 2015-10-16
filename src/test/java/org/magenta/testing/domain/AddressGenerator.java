package org.magenta.testing.domain;

import org.magenta.random.FluentRandom;

import com.google.common.base.Supplier;

public class AddressGenerator implements Supplier<Address> {

  private static int counter = 0;

  @Override
  public Address get() {
    Address address = new Address();
    address.setCity((counter++) + "-" + FluentRandom.strings().charabia(15));
    return address;
  }

}
